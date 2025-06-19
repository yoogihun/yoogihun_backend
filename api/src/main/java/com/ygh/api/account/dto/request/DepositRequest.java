package com.ygh.api.account.dto.request;

import com.ygh.account.domain.account.command.DepositCommand;

import java.math.BigDecimal;

public record DepositRequest(
        String accountNumber,
        BigDecimal amount
) {
    public DepositCommand toCommand() {
        return new DepositCommand(accountNumber, amount, false);
    }
}
