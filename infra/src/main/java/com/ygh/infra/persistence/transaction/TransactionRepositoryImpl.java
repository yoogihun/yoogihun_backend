package com.ygh.infra.persistence.transaction;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ygh.account.domain.account.entity.Account;
import com.ygh.common.enums.TransactionType;
import com.ygh.transaction.domain.account.entity.AccountTransaction;
import com.ygh.transaction.domain.account.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.ygh.transaction.domain.account.entity.QAccountTransaction.accountTransaction;


@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository, TransactionRepositoryCustom {

    private final TransactionJpaRepository transactionJpaRepository;
    private final JPAQueryFactory transactionQueryFactory;

    /**
     * 거래 내역 저장
     * @param accountTransaction
     * @return
     */
    @Override
    public AccountTransaction save(AccountTransaction accountTransaction) {
        return transactionJpaRepository.save(accountTransaction);
    }

    /**
     * 계좌 거래 내역 조회
     * @param accountId
     * @param pageable
     * @return
     */
    @Override
    public Page<AccountTransaction> findByAccountIdWithPagination(String accountId, Pageable pageable) {
        BooleanExpression isSender = accountTransaction.fromAccountId.eq(accountId);
        BooleanExpression isReceiver = accountTransaction.toAccountId.eq(accountId);
        BooleanExpression accountIdMatches = isSender.or(isReceiver);

        //페이징된 거래 목록 조회
        List<AccountTransaction> content = transactionQueryFactory
                .selectFrom(accountTransaction)
                .where(accountIdMatches)
                .orderBy(accountTransaction.transferredAt.desc().nullsLast()) // 최신순 + null 안전, null인 값은 맨 뒤로 보냄
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //전체 거래 수 계산
        Long total = transactionQueryFactory
                .select(accountTransaction.count())
                .from(accountTransaction)
                .where(accountIdMatches)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    /**
     * 일일 거래 내역 금액 합계 조회
     * @param account
     * @param date
     * @param type
     * @return
     */
    @Override
    public BigDecimal sumAmountByAccountAndDateAndType(Account account, LocalDate date, TransactionType type) {
        BigDecimal totalTransferAmount = transactionQueryFactory.select(accountTransaction.amount.sum())
                .from(accountTransaction)
                .where(
                        accountTransaction.fromAccountId.eq(account.getId()),
                        accountTransaction.transferredAt.between(date.atStartOfDay(), date.plusDays(1).atStartOfDay()),
                        accountTransaction.type.eq(type)
                )
                .fetchOne();

        return totalTransferAmount == null ? BigDecimal.ZERO : totalTransferAmount;
    }

}
