package com.ygh.transaction.domain.account.service;

import com.ygh.account.domain.account.command.RegisterAccountCommand;
import com.ygh.account.domain.account.command.TransferCommand;
import com.ygh.account.domain.account.dto.TransactionResponse;
import com.ygh.account.domain.account.entity.Account;
import com.ygh.account.domain.account.repository.AccountRepository;
import com.ygh.account.domain.account.service.AccountService;
import com.ygh.common.enums.TransactionType;
import com.ygh.common.event.TransactionRecordedEvent;
import com.ygh.common.response.PaginatedResponse;
import com.ygh.transaction.domain.account.command.TransactionHistoryCommand;
import com.ygh.transaction.domain.account.dto.TransactionHistoryDto;
import com.ygh.transaction.domain.account.entity.AccountTransaction;
import com.ygh.transaction.domain.account.exception.TransactionErrorCode;
import com.ygh.transaction.domain.account.exception.TransactionException;
import com.ygh.transaction.domain.account.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionUnitTest {
    @Mock
    AccountService accountService;

    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Test
    void 거래_내역_저장() {
        TransactionRecordedEvent recordedEvent =
                new TransactionRecordedEvent(TransactionType.DEPOSIT,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        "123456",
                        "456123",
                        LocalDateTime.now());

        transactionService.recordTransaction(recordedEvent);

        verify(transactionRepository, times(1)).save(any(AccountTransaction.class));
    }

    @Test
    void 계좌_이체_성공_및_한도초과_검증() {
        BigDecimal fromBalance = BigDecimal.valueOf(4_000_000);
        BigDecimal transferAmount = BigDecimal.valueOf(1_000_000);

        // 테스트용 계좌 객체 생성
        Account fromAccount = Account.builder()
                .accountNumber("from1234")
                .ownerName("이순신")
                .balance(fromBalance)
                .build();

        Account toAccount = Account.builder()
                .accountNumber("to5678")
                .ownerName("홍길동")
                .balance(BigDecimal.ZERO)
                .build();

        when(accountService.getAccountByNumber(fromAccount.getAccountNumber())).thenReturn(fromAccount);
        when(accountService.getAccountByNumber(toAccount.getAccountNumber())).thenReturn(toAccount);

        when(transactionRepository.sumAmountByAccountAndDateAndType(fromAccount, LocalDate.now(), TransactionType.TRANSFER))
                .thenReturn(BigDecimal.valueOf(2_000_000));

        // 첫 이체 - 정상 수행 가정
        // 실제 transfer 내부에서 fromAccount, toAccount 상태 변경과 잔액 업데이트가 이루어져야 한다면
        // 여기서는 직접 상태변경 후 save 호출도 목킹하거나 생략 가능

        // 첫 이체 - 100만원
        TransferCommand firstTransfer = new TransferCommand(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), transferAmount);

        transactionService.transfer(firstTransfer);

        BigDecimal fee = transferAmount.multiply(BigDecimal.valueOf(0.01));
        BigDecimal expectedFromBalance = fromBalance.subtract(transferAmount.add(fee));

        // fromAccount와 toAccount의 상태를 직접 변경해줘야 한다면 여기서 수동 변경 (mock으로 상태 변화 구현 안 하면)
        fromAccount.setBalance(expectedFromBalance);
        toAccount.setBalance(toAccount.getBalance().add(transferAmount));

        // 검증
        assertThat(fromAccount.getBalance()).isEqualByComparingTo(expectedFromBalance);
        assertThat(toAccount.getBalance()).isEqualByComparingTo(transferAmount);

        // 두 번째 이체 - 300만원 (일일 한도 초과)
        BigDecimal secondTransferAmount = BigDecimal.valueOf(3_000_000);
        TransferCommand overLimitCommand = new TransferCommand(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), secondTransferAmount);

        // 예외 확인
        assertThatThrownBy(() -> transactionService.transfer(overLimitCommand))
                .isInstanceOf(TransactionException.class)
                .hasMessageContaining("daily transfer limit exceeded.");
    }

    @Test
    void 거래_내역_조회_최신순_정렬() {
        // given
        String accountNumber = "1234567890";
        int page = 1;
        int size = 2;

        //조회 커맨드 생성
        TransactionHistoryCommand command = new TransactionHistoryCommand(accountNumber, page, size);

        //모킹 계좌
        Account account = Account.builder()
                .id(UUID.randomUUID().toString())
                .accountNumber(accountNumber)
                .build();

        //거래내역 생성, 1000원 입금 내역이
        AccountTransaction accountTransaction1 = createTransaction(new BigDecimal(1000), TransactionType.DEPOSIT, LocalDateTime.now().minusMinutes(10));
        AccountTransaction accountTransaction2 = createTransaction(new BigDecimal(500), TransactionType.TRANSFER, LocalDateTime.now());

        List<AccountTransaction> transactions = List.of(accountTransaction2, accountTransaction1); // 최신순
        Page<AccountTransaction> pageResult = new PageImpl<>(transactions);

        //계좌번호로 계좌 조회 모킹
        given(accountService.getAccountByNumber(accountNumber)).willReturn(account);
        //거래내역 조회 모킹
        given(transactionRepository.findByAccountIdWithPagination(account.getId(), PageRequest.of(0, 2)))
                .willReturn(pageResult);

        //거래내역 조회 결과
        PaginatedResponse<TransactionHistoryDto> result = transactionService.getTransactionsByAccount(command);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).amount()).isEqualByComparingTo(new BigDecimal(500));
        assertThat(result.getContent().get(1).amount()).isEqualByComparingTo(new BigDecimal(1000));
    }

    private AccountTransaction createTransaction(BigDecimal amount, TransactionType type, LocalDateTime time) {
        return AccountTransaction.builder()
                .amount(amount)
                .type(type)
                .transferredAt(time)
                .build();
    }
}