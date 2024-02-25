package com.sample.account.controller;

import com.sample.account.dto.CancelBalance;
import com.sample.account.dto.QueryTransactionResponse;
import com.sample.account.dto.UseBalance;
import com.sample.account.exception.AccountException;
import com.sample.account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 잔액 관련 컨트롤러
 * 잔액 사용
 * 잔액 사용 취소
 * 거래 확안
 */

@Slf4j
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/trasacntion/use")
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    ) {

        try {
            return UseBalance.Response.from(
                    transactionService.useBalance(
                            request.getUserId(),
                            request.getAccountNumber(), request.getAmount()
                    )
            );
        } catch (AccountException e) {
            log.error("Failed to use balance");
            transactionService.saveFailedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw  e;
        }
    }

    @PostMapping("/transaction/cancel")
    public CancelBalance.Response cancelBalance(
            @Valid @RequestBody CancelBalance.Request request
    ) {

        try {
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(
                            request.getTransactionId(),
                            request.getAccountNumber(), request.getAmount()
                    )
            );
        } catch (AccountException e) {
            log.error("Failed to use balance");
            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw  e;
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
             @PathVariable String transactionId
    ){
        return QueryTransactionResponse.from(transactionService.queryTransaction(transactionId)
        );
    }
}
