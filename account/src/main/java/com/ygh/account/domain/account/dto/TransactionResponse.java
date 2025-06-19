package com.ygh.account.domain.account.dto;

import com.ygh.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String transactionType,
        String beforeBalance,
        String afterBalance,
        String transactionAmount,
        String fee,
        LocalDateTime transactionTime
) {
    public static TransactionResponse of(
            TransactionType transactionType,
            BigDecimal beforeBalance,
            BigDecimal afterBalance,
            BigDecimal transactionAmount,
            BigDecimal fee,
            LocalDateTime transactionTime
    ) {
        return new TransactionResponse(
                transactionType.name(),
                beforeBalance.toPlainString(),
                afterBalance.toPlainString(),
                transactionAmount.toPlainString(),
                fee.toPlainString(),
                transactionTime
        );
    }
}
