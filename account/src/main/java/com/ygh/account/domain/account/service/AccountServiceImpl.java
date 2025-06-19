package com.ygh.account.domain.account.service;

import com.ygh.account.domain.account.command.DeleteAccountCommand;
import com.ygh.account.domain.account.command.DepositCommand;
import com.ygh.account.domain.account.command.RegisterAccountCommand;
import com.ygh.account.domain.account.command.WithdrawCommand;
import com.ygh.account.domain.account.dto.TransactionResponse;
import com.ygh.account.domain.account.entity.Account;
import com.ygh.account.domain.account.exception.AccountErrorCode;
import com.ygh.account.domain.account.exception.AccountException;
import com.ygh.account.domain.account.repository.AccountRepository;
import com.ygh.common.enums.TransactionType;
import com.ygh.common.event.TransactionRecordedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.ygh.account.domain.account.AccountNumberGenerator.numericAccountNumber;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(transactionManager = "accountTransactionManager") // transaction과 별도의 account transaction manager
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 계좌 생성
     *
     * @param command 계좌 생성 커맨드
     * @return 생성 완료 계좌 ID
     */
    @Override
    public String registerAccount(RegisterAccountCommand command) {
        //계좌 번호 생성
        String accountNumber = numericAccountNumber();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .ownerName(command.ownerName())
                .balance(BigDecimal.ZERO)
                .build();
        Account saveAccount = accountRepository.save(account);
        //생성 완료된 계좌 ID 반환
        return saveAccount.getId();
    }

    /**
     * 계좌 삭제
     *
     * @param command 계좌 삭제 커맨드
     */
    @Override
    public void deleteAccount(DeleteAccountCommand command) {
        //계좌 번호로 계좌 조회
        Account account = getAccountByNumber(command.accountNumber());
        //계좌 제거 soft delete, 계좌 이력은 추후 필요할 가능성이 존재하므로 제거하지 않음
        accountRepository.delete(account);
    }


    /**
     * 계좌 번호로 계좌 아이디 조회
     *
     * @param accountNumber
     * @return 계좌 객체 리턴
     */
    @Override
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    throw new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND);
                });
    }

    /**
     * 계좌 입금 처리
     *
     * @param command 계좌 입금
     */
    @Override
    public TransactionResponse deposit(DepositCommand command) {
        Account account = getAccountByNumber(command.accountNumber());
        BigDecimal beforeBalance = account.getBalance();

        account.deposit(command.amount());
        BigDecimal afterBalance = account.getBalance();

        BigDecimal fee = BigDecimal.ZERO;
        LocalDateTime transferredAt = LocalDateTime.now();

        if (!command.skipRecord()) {
            eventPublisher.publishEvent(new TransactionRecordedEvent(TransactionType.DEPOSIT, command.amount(), fee, null, account.getId(), transferredAt));
        }

        return TransactionResponse.of(TransactionType.DEPOSIT, beforeBalance, afterBalance, command.amount(), fee, transferredAt);
    }

    /**
     * 계좌 출금 처리
     *
     * @param command 계좌 출금
     */
    @Override
    public TransactionResponse withdraw(WithdrawCommand command) {
        Account account = getAccountByNumber(command.accountNumber());
        BigDecimal beforeBalance = account.getBalance();

        account.withdraw(command.amount());
        BigDecimal afterBalance = account.getBalance();

        BigDecimal fee = BigDecimal.ZERO;
        LocalDateTime transferredAt = LocalDateTime.now();

        if (!command.skipRecord()) {
            eventPublisher.publishEvent(new TransactionRecordedEvent(TransactionType.WITHDRAW, command.amount(), fee, account.getId(), null, transferredAt));
        }

        return TransactionResponse.of(TransactionType.WITHDRAW, beforeBalance, afterBalance, command.amount(), fee, transferredAt);
    }
}
