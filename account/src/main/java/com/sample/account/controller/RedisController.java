package com.sample.account.controller;

import com.sample.account.service.RedisServiceTest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RedisController {

    private final RedisServiceTest service;

    @GetMapping("/get-lock")
    public String getLock(){
        service.getLock();
        return "success";
    }
}
