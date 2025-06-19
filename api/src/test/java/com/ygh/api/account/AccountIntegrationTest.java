package com.ygh.api.account;

import com.ygh.account.domain.account.command.DeleteAccountCommand;
import com.ygh.account.domain.account.command.DepositCommand;
import com.ygh.account.domain.account.command.RegisterAccountCommand;
import com.ygh.account.domain.account.command.WithdrawCommand;
import com.ygh.account.domain.account.entity.Account;
import com.ygh.account.domain.account.exception.AccountException;
import com.ygh.account.domain.account.repository.AccountRepository;
import com.ygh.account.domain.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountIntegrationTest {
    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    void 계좌_생성_입금() {
        String accountId = accountService.registerAccount(new RegisterAccountCommand("홍길동", "국민은행"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssertionError("Account not found."));

        accountService.deposit(new DepositCommand(account.getAccountNumber(), BigDecimal.valueOf(1000), false));

        Account updatedAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssertionError("Account not found."));

        assertThat(updatedAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void 계좌_삭제() {
        // 계좌 생성
        String accountId = accountService.registerAccount(new RegisterAccountCommand("홍길동", "국민은행"));

        // 실제 저장된 계좌 조회
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssertionError("Account not found."));

        // 해당 계좌의 accountNumber로 삭제
        accountService.deleteAccount(new DeleteAccountCommand(account.getAccountNumber()));
    }

    @Test
    void 계좌_출금() {
        String accountId = accountService.registerAccount(new RegisterAccountCommand("김철수", "신한은행"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssertionError("Account not found."));

        // 입금 먼저
        accountService.deposit(new DepositCommand(account.getAccountNumber(), BigDecimal.valueOf(5000), false));

        // 출금
        accountService.withdraw(new WithdrawCommand(account.getAccountNumber(), BigDecimal.valueOf(3000), false));

        Account updatedAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssertionError("Account not found."));

        assertThat(updatedAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(2000));
    }

    @Test
    void 계좌_출금_잔액_부족() {
        //계좌 생성
        String accountId = accountService.registerAccount(new RegisterAccountCommand("출금한도", "국민은행"));
        //계좌 조회
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssertionError("Account not found."));
        //계좌 200만원 입금
        accountService.deposit(new DepositCommand(account.getAccountNumber(), BigDecimal.valueOf(2_000_000), false));
        //계좌 250만원 출금
        assertThatThrownBy(() -> accountService.withdraw(new WithdrawCommand(account.getAccountNumber(), BigDecimal.valueOf(2_500_000), false)))
                .isInstanceOf(AccountException.class)
                .hasMessageContaining("Not enough balance to proceed.");
    }
}