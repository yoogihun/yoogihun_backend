package com.ygh.account.domain.account.entity;

import com.ygh.account.domain.account.exception.AccountErrorCode;
import com.ygh.account.domain.account.exception.AccountException;
import com.ygh.common.jpa.BaseEntity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "account", indexes = {
        @Index(name = "idx_account_number", columnList = "account_number", unique = true)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@Where(clause = "deleted = 0")
@SQLDelete(sql = "update account set deleted = 1 where id = ?")
@SuperBuilder
public class Account extends BaseEntity {

    @Column(name = "account_number", nullable = false, unique = true)
    @Comment("계좌 번호")
    private String accountNumber;

    @Column(name="owner_name", nullable = false)
    @Comment("계좌 소유자 이름")
    private String ownerName;

    @Column(name="balance", nullable = false, precision = 19, scale = 2)
    @Comment("잔액")
    @Setter
    private BigDecimal balance;

    /**
     * 출금
     *
     * @param amount
     */
    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new AccountException(AccountErrorCode.INSUFFICIENT_FUNDS);
        }
        this.balance = this.balance.subtract(amount);
    }

    /**
     * 입금
     *
     * @param amount
     */
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountException(AccountErrorCode.INVALID_AMOUNT);
        }
        this.balance = this.balance.add(amount);
    }

}
