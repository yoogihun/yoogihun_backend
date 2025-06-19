package com.ygh.account.domain.account.command;

import java.math.BigDecimal;

public record TransferCommand(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount
) {
}
