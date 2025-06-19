package com.ygh.transaction.domain.account.entity;

import com.ygh.common.enums.TransactionType;
import com.ygh.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "account_transaction", indexes = {
        @Index(name = "idx_from_account_id", columnList = "from_account_id"),
        @Index(name = "idx_to_account_id", columnList = "to_account_id"),
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_transferredAt", columnList = "transferred_at")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@Where(clause = "deleted = 0")
@SQLDelete(sql = "update account_transaction set deleted = 1, where id = ?")
@SuperBuilder
public class AccountTransaction extends BaseEntity {

    @Column(name = "from_account_id")
    private String fromAccountId;

    @Column(name = "to_account_id")
    private String toAccountId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "fee", nullable = false)
    private BigDecimal fee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "transferred_at", nullable = false)
    private LocalDateTime transferredAt;
}
