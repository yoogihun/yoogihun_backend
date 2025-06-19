package com.ygh.account.domain.account.command;

public record RegisterAccountCommand(
        String ownerName,
        String bankName
) {
}
