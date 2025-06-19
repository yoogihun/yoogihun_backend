package com.ygh.api.account.dto.request;

import com.ygh.account.domain.account.command.WithdrawCommand;

import java.math.BigDecimal;

public record WithdrawRequest(
        String accountNumber,
        BigDecimal amount
) {
    public WithdrawCommand toCommand() {
        return new WithdrawCommand(accountNumber, amount, false);
    }
}
