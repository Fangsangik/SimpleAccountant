package com.sample.account.service;

import com.sample.account.aop.AccountLockInterface;
import com.sample.account.dto.CancelBalance;
import com.sample.account.dto.UseBalance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

    private final LockService lockService;

    @Around("@annotation(com.sample.account.aop.AccountLock) && args(request)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp,
            AccountLockInterface request
            ) throws Throwable {

        //lock 취둑 시도
        try {
            lockService.lock(request.getAccountNumber());
            return pjp.proceed();
        } finally {
            //lock 해제
            lockService.unlock(request.getAccountNumber());
        }
    }
}
