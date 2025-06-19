package com.ygh.api.transaction;

import com.ygh.account.domain.account.command.DepositCommand;
import com.ygh.account.domain.account.command.RegisterAccountCommand;
import com.ygh.account.domain.account.command.TransferCommand;
import com.ygh.account.domain.account.entity.Account;
import com.ygh.account.domain.account.exception.AccountException;
import com.ygh.account.domain.account.repository.AccountRepository;
import com.ygh.account.domain.account.service.AccountService;
import com.ygh.common.response.PaginatedResponse;
import com.ygh.transaction.domain.account.command.TransactionHistoryCommand;
import com.ygh.transaction.domain.account.dto.TransactionHistoryDto;
import com.ygh.transaction.domain.account.exception.TransactionException;
import com.ygh.transaction.domain.account.service.TransactionService;
import com.ygh.transaction.domain.account.service.TransactionServiceImpl;
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
class TransactionIntegrationTest {
    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionService transactionService;

    @Test
    void 계좌_이체_성공_및_한도초과_검증() {
        BigDecimal fromBalance = BigDecimal.valueOf(4_000_000);
        BigDecimal transferAmount = BigDecimal.valueOf(1_000_000);
        // 계좌 등록
        String fromId = accountService.registerAccount(new RegisterAccountCommand("이순신", "하나은행"));
        String toId = accountService.registerAccount(new RegisterAccountCommand("김철수", "우리은행"));

        Account from = accountRepository.findById(fromId)
                .orElseThrow(() -> new AssertionError("Account not found."));
        Account to = accountRepository.findById(toId)
                .orElseThrow(() -> new AssertionError("Account not found."));

        // 초기 입금
        accountService.deposit(new DepositCommand(from.getAccountNumber(), fromBalance, false));

        // 첫 이체 100만원 (성공)
        transactionService.transfer(new TransferCommand(from.getAccountNumber(), to.getAccountNumber(), transferAmount));

        Account updatedFrom = accountService.getAccountByNumber(from.getAccountNumber());
        Account updatedTo = accountService.getAccountByNumber(to.getAccountNumber());

        BigDecimal fee = transferAmount.multiply(BigDecimal.valueOf(0.01)); // 1% 수수료

        //400만원 - 수수료 포함 101만원 = 299만원
        BigDecimal expectedFromBalance = fromBalance.subtract(transferAmount.add(fee));
        //새로 조회된 from, to 계좌의 잔액 비교
        assertThat(updatedFrom.getBalance()).isEqualByComparingTo(expectedFromBalance);
        assertThat(updatedTo.getBalance()).isEqualByComparingTo(transferAmount);

        // 두 번째 이체 - 300만원
        BigDecimal secondTransfer = BigDecimal.valueOf(3_000_000);
        TransferCommand overLimitCommand = new TransferCommand(from.getAccountNumber(), to.getAccountNumber(), secondTransfer);

        //일일 이체 한도 초과
        assertThatThrownBy(() -> transactionService.transfer(overLimitCommand))
                .isInstanceOf(TransactionException.class)
                .hasMessageContaining("daily transfer limit exceeded.");
    }

    @Test
    void 거래_내역_최신순_조회() {
        String accountId = accountService.registerAccount(new RegisterAccountCommand("박영희", "하나은행"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AssertionError("Account not found."));

        accountService.deposit(new DepositCommand(account.getAccountNumber(), new BigDecimal(5000), false));
        accountService.deposit(new DepositCommand(account.getAccountNumber(), new BigDecimal(10000), false));

        PaginatedResponse<TransactionHistoryDto> result = transactionService.getTransactionsByAccount(new TransactionHistoryCommand(account.getAccountNumber(),1,10));
        assertThat(result.getContent().get(0).amount()).isEqualByComparingTo(new BigDecimal(10000));
        assertThat(result.getContent().get(1).amount()).isEqualByComparingTo(new BigDecimal(5000));
    }
}