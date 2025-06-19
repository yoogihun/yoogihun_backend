package com.ygh.transaction.domain.account.eventhandler;

import com.ygh.common.event.TransactionRecordedEvent;
import com.ygh.transaction.domain.account.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TransactionEventHandler {
    private final TransactionService transactionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void recordTransactionHandle(TransactionRecordedEvent event) {
        transactionService.recordTransaction(event);
    }
}
