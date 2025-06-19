package com.ygh.api.account.dto.request;

import com.ygh.account.domain.account.command.TransferCommand;

import java.math.BigDecimal;

public record TransferRequest(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount
) {
    public TransferCommand toCommand() {
        return new TransferCommand(fromAccountNumber, toAccountNumber, amount);
    }
}
