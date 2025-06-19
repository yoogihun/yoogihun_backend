package com.ygh.account.domain.account.repository;

import com.ygh.account.domain.account.entity.Account;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(String id);
    Optional<Account> findByAccountNumber(String accountNumber);
    Account save(Account account);
    void delete(Account account);
}
