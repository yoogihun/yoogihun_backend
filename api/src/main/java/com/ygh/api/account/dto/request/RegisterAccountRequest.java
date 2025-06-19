package com.ygh.api.account.dto.request;

import com.ygh.account.domain.account.command.RegisterAccountCommand;

public record RegisterAccountRequest(
        String ownerName,
        String bankName
) {
    public RegisterAccountCommand toCommand() {
        return new RegisterAccountCommand(ownerName, bankName);
    }
}
