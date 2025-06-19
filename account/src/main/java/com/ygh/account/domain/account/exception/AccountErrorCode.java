package com.ygh.account.domain.account.exception;

import com.ygh.common.exception.BaseErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum AccountErrorCode implements BaseErrorCode {
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "10000", "Account not found."), //계좌 찾기 실패
    INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "10001", "Not enough balance to proceed."), //잔액 부족
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "10002", "invalid amount."), //올바르지 않은 금액(음수)
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
