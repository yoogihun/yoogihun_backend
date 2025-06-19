package com.ygh.transaction.domain.account.exception;

import com.ygh.common.exception.BaseException;

public class TransactionException extends BaseException {
    public TransactionException(TransactionErrorCode errorCode) {
        super(errorCode);
    }
}
