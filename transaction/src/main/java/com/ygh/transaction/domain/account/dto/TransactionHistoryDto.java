package com.ygh.transaction.domain.account.dto;

import com.ygh.transaction.domain.account.entity.AccountTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistoryDto(
        String transactionId,
        String type,
        BigDecimal amount,
        BigDecimal fee,
        LocalDateTime transferredAt,
        String fromAccountId,
        String toAccountId
) {
    public static TransactionHistoryDto fromDomain(AccountTransaction domain) {
        return new TransactionHistoryDto(
                domain.getId(),
                domain.getType().name(),
                domain.getAmount(),
                domain.getFee(),
                domain.getTransferredAt(),
                domain.getFromAccountId(),
                domain.getToAccountId()
        );
    }
}
