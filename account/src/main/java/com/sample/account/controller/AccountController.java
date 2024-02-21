package com.sample.account.controller;

import com.sample.account.domain.Account;
import com.sample.account.service.AccountService;
import dto.CreateAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;
    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
            ) {

        return CreateAccount.Response.from(service.createAccount(
                request.getUserId(),
                request.getInitialBalance()));
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id){
        return service.getAccount(id);
    }
}
