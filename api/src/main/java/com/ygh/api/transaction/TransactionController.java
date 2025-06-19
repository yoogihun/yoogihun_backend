package com.ygh.api.transaction;

import com.ygh.account.domain.account.dto.TransactionResponse;
import com.ygh.api.account.dto.request.TransferRequest;
import com.ygh.api.transaction.dto.request.TransactionHistoryRequest;
import com.ygh.common.response.PaginatedResponse;
import com.ygh.common.response.Result;
import com.ygh.transaction.domain.account.dto.TransactionHistoryDto;
import com.ygh.transaction.domain.account.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transaction", description = "거래 관련 API")
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
    @Operation(summary = "이체", description = "계좌 이체를 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "계좌 이체 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json")),
    })
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
    @Operation(summary = "거래내역 조회", description = "거래내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거래내역 조회 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json")),
    })
    @GetMapping("/history")
    public ResponseEntity<Result> getTransactionsByAccount(@Valid TransactionHistoryRequest request) {
        PaginatedResponse<TransactionHistoryDto> transactions = transactionService.getTransactionsByAccount(request.toCommand());
        return ResponseEntity.ok(new Result(transactions));
    }
}
