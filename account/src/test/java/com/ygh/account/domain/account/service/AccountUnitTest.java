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
import com.ygh.common.event.TransactionRecordedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountUnitTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AccountServiceImpl accountService;

    /**
     * 계좌 생성
     */
    @Test
    @DisplayName("계좌 생성 성공")
    void 계좌_생성_성공() {
        RegisterAccountCommand command = new RegisterAccountCommand("홍길동", "우리은행");
        Account savedAccount = Account.builder()
                .id("abc123")
                .accountNumber("1234567890")
                .ownerName("홍길동")
                .balance(BigDecimal.ZERO)
                .build();

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        String accountNumber = accountService.registerAccount(command);

        assertThat(accountNumber).isEqualTo("1234567890");
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    /**
     * 계좌 삭제
     */
    @Test
    @DisplayName("계좌 삭제 성공")
    void 계좌_삭제_성공() {
        Account account = Account.builder()
                .id("abc123")
                .accountNumber("1234567890")
                .ownerName("홍길동")
                .balance(BigDecimal.ZERO)
                .build();

        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(account));

        doNothing().when(accountRepository).delete(account);

        accountService.deleteAccount(new DeleteAccountCommand("1234567890"));

        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    @DisplayName("계좌 삭제 시 계좌를 찾지 못하면 예외 발생")
    void 계좌_삭제_계좌찾기실패() {
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.deleteAccount(new DeleteAccountCommand("999999")))
                .isInstanceOf(AccountException.class)
                .hasMessageContaining("Account not found.");
    }

    @Test
    @DisplayName("계좌번호로 계좌 조회")
    void 계좌_조회() {
        Account account = Account.builder()
                .id("abc123")
                .accountNumber("1234567890")
                .ownerName("홍길동")
                .balance(BigDecimal.ZERO)
                .build();

        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(account));

        Account result = accountService.getAccountByNumber("1234567890");

        assertThat(result.getId()).isEqualTo("abc123");
    }

    @Test
    @DisplayName("계좌번호로 계좌 조회")
    void 계좌_조회_실패() {
        when(accountRepository.findByAccountNumber("000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountByNumber("000000"))
                .isInstanceOf(AccountException.class)
                .hasMessageContaining("Account not found.");
    }

    @Test
    @DisplayName("계좌 입금 성공")
    void 입금_성공() {
        Account account = spy(Account.builder()
                .accountNumber("1234567890")
                .ownerName("홍길동")
                .balance(BigDecimal.ZERO)
                .build());

        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(account));

        DepositCommand command = new DepositCommand("1234567890", BigDecimal.valueOf(1000), false);

        TransactionResponse response = accountService.deposit(command);

        verify(account, times(1)).deposit(BigDecimal.valueOf(1000));
        verify(eventPublisher, times(1)).publishEvent(any(TransactionRecordedEvent.class));
        assertThat(new BigDecimal(response.afterBalance())).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("계좌 출금 성공")
    void 출금_성공() {
        Account account = spy(Account.builder()
                .accountNumber("1234567890")
                .ownerName("홍길동")
                .balance(BigDecimal.valueOf(2000))
                .build());

        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(account));

        WithdrawCommand command = new WithdrawCommand("1234567890", BigDecimal.valueOf(1000), false);

        TransactionResponse response = accountService.withdraw(command);

        verify(account, times(1)).withdraw(BigDecimal.valueOf(1000));
        verify(eventPublisher, times(1)).publishEvent(any(TransactionRecordedEvent.class));
        assertThat(new BigDecimal(response.afterBalance())).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("출금 시 잔액이 부족한 경우")
    void 출금_잔액부족() {
        Account account = spy(Account.builder()
                .accountNumber("1234567890")
                .ownerName("홍길동")
                .balance(BigDecimal.valueOf(500))
                .build());

        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(account));

        WithdrawCommand command = new WithdrawCommand("1234567890", BigDecimal.valueOf(1000), false);

        doThrow(new AccountException(AccountErrorCode.INSUFFICIENT_FUNDS)).when(account).withdraw(any());

        assertThatThrownBy(() -> accountService.withdraw(command))
                .isInstanceOf(AccountException.class)
                .hasMessageContaining("Not enough balance to proceed.");
    }
}