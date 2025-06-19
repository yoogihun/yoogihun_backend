package com.ygh.common.enums;

/**
 * 거래 유형을 나타내는 열거형(enum)
 * 입금(DEPOSIT), 출금(WITHDRAW), 이체(TRANSFER) 세 가지 유형
 */
public enum TransactionType {
    DEPOSIT, // 입금
    WITHDRAW, // 출금
    TRANSFER // 이체
    ;

    /**
     * 해당 거래가 '입금' 유형인지
     * 입금일 경우 true, 그 외에는 false
     */
    public boolean isCredit() {
        return this == DEPOSIT;
    }

    /**
     * 해당 거래가 '출금' 또는 '이체' 유형인지
     * 출금 또는 이체일 경우 true, 입금이면 false
     */
    public boolean isDebit() {
        return this == WITHDRAW || this == TRANSFER;
    }
}
