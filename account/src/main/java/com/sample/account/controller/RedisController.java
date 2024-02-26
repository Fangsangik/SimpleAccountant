package com.sample.account.controller;

import com.sample.account.aop.AccountLockInterface;
import com.sample.account.service.LockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RedisController {

    private final LockService service;
    private final AccountLockInterface accountNumber;

    @GetMapping("/get-lock")
    public String getLock(){
        service.lock(accountNumber.getAccountNumber());
        return "success";
    }
}
