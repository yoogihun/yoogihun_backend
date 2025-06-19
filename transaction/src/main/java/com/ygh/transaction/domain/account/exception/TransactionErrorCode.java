package com.ygh.transaction.domain.account.exception;

import com.ygh.common.exception.BaseErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum TransactionErrorCode implements BaseErrorCode {
    TRANSFER_LIMIT(HttpStatus.BAD_REQUEST, "20000", "daily transfer limit exceeded.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
