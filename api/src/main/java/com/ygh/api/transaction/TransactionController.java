package com.ygh.api.transaction;

import com.ygh.account.domain.account.dto.TransactionResponse;
import com.ygh.api.account.dto.request.TransferRequest;
import com.ygh.api.transaction.dto.request.TransactionHistoryRequest;
import com.ygh.common.response.PaginatedResponse;
import com.ygh.common.response.Result;
import com.ygh.transaction.domain.account.dto.TransactionHistoryDto;
import com.ygh.transaction.domain.account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 이체 API
     *
     * @param request
     * @return
     */
    @PostMapping("/transfer")
    public ResponseEntity<Result> transfer(@RequestBody TransferRequest request) {
        TransactionResponse response = transactionService.transfer(request.toCommand());
        return ResponseEntity.ok(new Result(response));
    }

    /**
     * 거래내역 조회 API (계좌 기준, 최신순)
     *
     * @param request
     * @return
     */
    @GetMapping("/history")
    public ResponseEntity<Result> getTransactionsByAccount(@Valid TransactionHistoryRequest request) {
        PaginatedResponse<TransactionHistoryDto> transactions = transactionService.getTransactionsByAccount(request.toCommand());
        return ResponseEntity.ok(new Result(transactions));
    }
}
