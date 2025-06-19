package com.ygh.common.event;

import com.ygh.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRecordedEvent(
        TransactionType type,
        BigDecimal amount,
        BigDecimal fee,
        String fromAccountId,
        String toAccountId,
        LocalDateTime transferredAt
) {
}
