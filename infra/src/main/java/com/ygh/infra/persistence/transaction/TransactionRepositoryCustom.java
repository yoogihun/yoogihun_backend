package com.ygh.infra.persistence.transaction;

import com.ygh.account.domain.account.entity.Account;
import com.ygh.common.enums.TransactionType;
import com.ygh.transaction.domain.account.entity.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionRepositoryCustom {
    Page<AccountTransaction> findByAccountIdWithPagination(String accountId, Pageable pageable);
    BigDecimal sumAmountByAccountAndDateAndType(Account account, LocalDate date, TransactionType type);
}
