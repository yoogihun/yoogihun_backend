package com.ygh.transaction.domain.account.command;

public record TransactionHistoryCommand(
        String accountNumber,  // 또는 accountId
        int page,
        int size
) {
}
