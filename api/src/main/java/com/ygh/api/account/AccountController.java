package com.ygh.api.account;

import com.ygh.account.domain.account.dto.TransactionResponse;
import com.ygh.account.domain.account.service.AccountService;
import com.ygh.api.account.dto.request.DeleteAccountRequest;
import com.ygh.api.account.dto.request.DepositRequest;
import com.ygh.api.account.dto.request.RegisterAccountRequest;
import com.ygh.api.account.dto.request.WithdrawRequest;
import com.ygh.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Account", description = "계좌 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @Operation(summary = "계좌 생성", description = "새로운 계좌를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계좌 생성 성공, 계좌번호 리턴",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/account")
    public ResponseEntity<Result> registerAccount(@RequestBody RegisterAccountRequest registerAccountRequest) {
        String accountNumber = accountService.registerAccount(registerAccountRequest.toCommand());
        return ResponseEntity.ok(new Result(accountNumber));
    }

    @Operation(summary = "계좌 삭제", description = "계좌번호로 계좌를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "계좌 삭제 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json")),
    })
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        accountService.deleteAccount(new DeleteAccountRequest(accountNumber).toCommand());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "입금", description = "계좌에 입금합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입금 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json")),
    })
    @PostMapping("/deposit")
    public ResponseEntity<Result> deposit(@RequestBody DepositRequest request) {
        TransactionResponse response = accountService.deposit(request.toCommand());
        return ResponseEntity.ok(new Result(response));
    }

    @Operation(summary = "출금", description = "계좌에서 출금합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출금 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json")),
    })
    @PostMapping("/withdraw")
    public ResponseEntity<Result> withdraw(@RequestBody WithdrawRequest request) {
        TransactionResponse response = accountService.withdraw(request.toCommand());
        return ResponseEntity.ok(new Result(response));
    }
}
