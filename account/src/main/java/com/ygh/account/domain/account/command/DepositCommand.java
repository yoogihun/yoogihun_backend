package com.ygh.account.domain.account.command;

import java.math.BigDecimal;

public record DepositCommand(
        String accountNumber,
        BigDecimal amount,
        boolean skipRecord
) {
}
