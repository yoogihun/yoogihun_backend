package com.ygh.account.domain.account.repository;

import com.ygh.account.domain.account.entity.Account;
import com.ygh.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(String id);
    Optional<Account> findByAccountNumber(String accountNumber);
    Account save(Account account);
    void delete(Account account);
}
