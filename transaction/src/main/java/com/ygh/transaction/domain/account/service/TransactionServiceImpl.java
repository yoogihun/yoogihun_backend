package com.ygh.transaction.domain.account.service;

import com.ygh.account.domain.account.command.DepositCommand;
import com.ygh.account.domain.account.command.TransferCommand;
import com.ygh.account.domain.account.command.WithdrawCommand;
import com.ygh.account.domain.account.dto.TransactionResponse;
import com.ygh.account.domain.account.entity.Account;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(transactionManager = "transactionTransactionManager") //Account와 별개의 트랜잭션 매니저
public class TransactionServiceImpl implements TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private static final int TRANSFER_LIMIT = 3000000;

    @Override
    public void recordTransaction(TransactionRecordedEvent transactionRecordedEvent) {
        AccountTransaction depositAccountTransaction = AccountTransaction.builder()
                .type(transactionRecordedEvent.type())
                .amount(transactionRecordedEvent.amount())
                .fee(transactionRecordedEvent.fee()) // 입금 수수료 0원
                .fromAccountId(transactionRecordedEvent.fromAccountId())  // 외부에서 들어온 돈
                .toAccountId(transactionRecordedEvent.toAccountId())
                .transferredAt(LocalDateTime.now())
                .build();
        //거래 내역 저장
        transactionRepository.save(depositAccountTransaction);
    }

    /**
     * 계좌 이체
     *
     * @param command
     * @return
     */
    @Override
    public TransactionResponse transfer(TransferCommand command) {
        Account fromAccount = accountService.getAccountByNumber(command.fromAccountNumber());
        BigDecimal beforeBalance = fromAccount.getBalance();

        LocalDate nowDate = LocalDate.now();
        BigDecimal totalTransferAmount = transactionRepository.sumAmountByAccountAndDateAndType(fromAccount, nowDate, TransactionType.TRANSFER);

        //일일 300만원 이상인 경우 예외 발생
        if (totalTransferAmount.add(command.amount()).intValue() > TRANSFER_LIMIT) {
            throw new TransactionException(TransactionErrorCode.TRANSFER_LIMIT);
        }

        Account toAccount = accountService.getAccountByNumber(command.toAccountNumber());

        //수수료는 이체금액의 1%
        BigDecimal fee = command.amount().multiply(BigDecimal.valueOf(0.01));
        //계좌에서 금액+수수료 만큼 차감
        accountService.withdraw(new WithdrawCommand(command.fromAccountNumber(), command.amount().add(fee), true));
        BigDecimal afterBalance = fromAccount.getBalance();

        //목표 계좌에 금액만큼 입금
        accountService.deposit(new DepositCommand(command.toAccountNumber(), command.amount(), true));
        //이체 일시
        LocalDateTime transferredAt = LocalDateTime.now();
        //거래 내역 저장
        recordTransaction(new TransactionRecordedEvent(TransactionType.TRANSFER, command.amount(), BigDecimal.ZERO, fromAccount.getId(), toAccount.getId(), transferredAt));
        return TransactionResponse.of(TransactionType.TRANSFER, beforeBalance, afterBalance, command.amount(), fee, transferredAt);
    }

    /**
     * 송금, 수취 내역 조회
     * @param command
     * @return
     */
    @Override
    public PaginatedResponse<TransactionHistoryDto> getTransactionsByAccount(TransactionHistoryCommand command) {
        //pageable 객체 생성, page는 1부터 시작하므로 -1 적용
        Pageable pageable = PageRequest.of(command.page()-1, command.size());
        //계좌 번호로 계좌 조회
        Account account = accountService.getAccountByNumber(command.accountNumber());
        //Pagination 거래내역 조회
        Page<AccountTransaction> accountTransactionPage = transactionRepository.findByAccountIdWithPagination(account.getId(), pageable);
        //pagination Dto로 변환
        Page<TransactionHistoryDto> transactionHistoryDtoPage = accountTransactionPage.map(accountTransaction -> TransactionHistoryDto.fromDomain(accountTransaction));
        //pagination Response 반환
        return new PaginatedResponse(transactionHistoryDtoPage);
    }
}
