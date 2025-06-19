package com.ygh.account.domain.account.exception;

import com.ygh.common.exception.BaseException;

/**
 * 계좌 예외 관련 Exception
 */
public class AccountException extends BaseException {
    public AccountException(AccountErrorCode errorCode) {
        super(errorCode);
    }
}
