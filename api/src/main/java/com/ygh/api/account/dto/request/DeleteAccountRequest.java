package com.ygh.api.account.dto.request;

import com.ygh.account.domain.account.command.DeleteAccountCommand;

public record DeleteAccountRequest(
        String accountNumber
) {
    public DeleteAccountCommand toCommand() {
        return new DeleteAccountCommand(accountNumber);
    }
}
