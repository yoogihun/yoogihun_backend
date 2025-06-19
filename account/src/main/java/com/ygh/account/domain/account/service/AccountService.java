package com.ygh.account.domain.account.service;

import com.ygh.account.domain.account.command.DeleteAccountCommand;
import com.ygh.account.domain.account.command.DepositCommand;
import com.ygh.account.domain.account.command.RegisterAccountCommand;
import com.ygh.account.domain.account.command.WithdrawCommand;
import com.ygh.account.domain.account.dto.TransactionResponse;
import com.ygh.account.domain.account.entity.Account;

public interface AccountService {
    /**
     * 계좌 생성
     *
     * @param command
     * @return
     */
    String registerAccount(RegisterAccountCommand command);

    /**
     * 계좌 삭제
     *
     * @param command
     */
    void deleteAccount(DeleteAccountCommand command);

    /**
     * 계좌 번호로 계좌 조회
     * @param accountNumber
     * @return
     */
    Account getAccountByNumber(String accountNumber);

    /**
     * 계좌 입금
     * @param command
     * @return
     */
    TransactionResponse deposit(DepositCommand command);

    /**
     * 계좌 출금
     * @param command
     * @return
     */
    TransactionResponse withdraw(WithdrawCommand command);
}
