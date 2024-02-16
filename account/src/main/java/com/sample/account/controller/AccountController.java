package com.sample.account.controller;

import com.sample.account.domain.Account;
import com.sample.account.service.AccountService;
import com.sample.account.service.RedisServiceTest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;
    @GetMapping("/create-account")
    public String createAccount(){
        service.createAccount();

        return "success";
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id){
        return service.getAccount(id);
    }
}
