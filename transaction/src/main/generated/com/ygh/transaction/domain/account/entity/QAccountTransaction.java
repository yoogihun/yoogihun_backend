package com.ygh.transaction.domain.account.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAccountTransaction is a Querydsl query type for AccountTransaction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountTransaction extends EntityPathBase<AccountTransaction> {

    private static final long serialVersionUID = -559211902L;

    public static final QAccountTransaction accountTransaction = new QAccountTransaction("accountTransaction");

    public final com.ygh.common.jpa.QBaseEntity _super = new com.ygh.common.jpa.QBaseEntity(this);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath deleted = _super.deleted;

    public final NumberPath<java.math.BigDecimal> fee = createNumber("fee", java.math.BigDecimal.class);

    public final StringPath fromAccountId = createString("fromAccountId");

    //inherited
    public final StringPath id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath toAccountId = createString("toAccountId");

    public final DateTimePath<java.time.LocalDateTime> transferredAt = createDateTime("transferredAt", java.time.LocalDateTime.class);

    public final EnumPath<com.ygh.common.enums.TransactionType> type = createEnum("type", com.ygh.common.enums.TransactionType.class);

    public QAccountTransaction(String variable) {
        super(AccountTransaction.class, forVariable(variable));
    }

    public QAccountTransaction(Path<? extends AccountTransaction> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccountTransaction(PathMetadata metadata) {
        super(AccountTransaction.class, metadata);
    }

}

