package com.ygh.transaction.domain.account.service;

import com.ygh.account.domain.account.command.TransferCommand;
import com.ygh.account.domain.account.dto.TransactionResponse;
import com.ygh.common.event.TransactionRecordedEvent;
import com.ygh.common.response.PaginatedResponse;
import com.ygh.transaction.domain.account.command.TransactionHistoryCommand;
import com.ygh.transaction.domain.account.dto.TransactionHistoryDto;

public interface TransactionService {
    void recordTransaction(TransactionRecordedEvent transactionRecordedEvent);
    TransactionResponse transfer(TransferCommand command);
    PaginatedResponse<TransactionHistoryDto> getTransactionsByAccount(TransactionHistoryCommand command);
}
