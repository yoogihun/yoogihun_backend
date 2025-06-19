package com.ygh.api.transaction.dto.request;

import com.ygh.transaction.domain.account.command.TransactionHistoryCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record TransactionHistoryRequest(
        @NotBlank String accountNumber,
        @Min(1) int page,
        @Min(1) int size
) {
    public TransactionHistoryCommand toCommand() {
        return new TransactionHistoryCommand(accountNumber, page, size);
    }
}
