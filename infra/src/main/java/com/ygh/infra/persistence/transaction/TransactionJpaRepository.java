package com.ygh.infra.persistence.transaction;

import com.ygh.transaction.domain.account.entity.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionJpaRepository extends JpaRepository<AccountTransaction, String> {
    Optional<AccountTransaction> findById(String id);
    AccountTransaction save(AccountTransaction accountTransaction);
    List<AccountTransaction> findAllByFromAccountId(String accountId);

}
