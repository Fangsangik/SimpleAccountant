package com.sample.account.controller;

import com.sample.account.domain.Account;
import com.sample.account.dto.AccountInfo;
import com.sample.account.dto.DeleteAccount;
import com.sample.account.service.AccountService;
import com.sample.account.dto.CreateAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;
    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
            ) {

      return CreateAccount.Response.from(
              service.createAccount(
                      request.getUserId(),
                      request.getInitialBalance()
              )
      );
    }

    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request
    ) {

        return DeleteAccount.Response.from(
                service.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountByUserId(
            @RequestParam("userId") Long userId){
       return service.getAccountsByUserId(userId).stream().map(accountDto -> AccountInfo.builder()
                .accountNumber(accountDto.getAccountNumber())
                .balance(accountDto.getBalance())
                .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id){
        return service.getAccount(id);
    }
}
