package com.ygh.account.domain.account;

import java.security.SecureRandom;
import java.util.UUID;

public class AccountNumberGenerator {
    private static final SecureRandom random = new SecureRandom();

    // 숫자만 사용하는 계좌번호 생성 (12자리)
    public static String numericAccountNumber() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10)); // 0~9
        }
        return sb.toString();
    }

    // UUID 기반 계좌번호 생성 (영문+숫자 포함, 12자리)
    public static String uuidAccountNumber() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
    }
}
