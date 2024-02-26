package com.sample.account.service;

import com.sample.account.exception.AccountException;
import com.sample.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient client;

    public void lock(String accountNumber) {
        RLock lock = client.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber : {}", accountNumber);

        try {
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
            //시도는 최대 1초 동안 시도,
            //lock을 획득을 하면 3초 동안 소유 하고 있다가, 풀어줌
            //unlock을 해주지 않았기 때문에 다른게 lock을 소유 하려고 하면 실패
            if (!isLock) {
                log.error("Lock acquisition failed");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        } catch (AccountException e) {
            throw e;

            //다른 error가 발생 했을때
        } catch (Exception e) {
            log.error("lock failed", e);
        }
    }

    private String getLockKey(String accountNumber) {
        return "ACLK : " + accountNumber;
    }

    public void unlock(String accountNumber) {
        log.debug("Unlock for accountNumber : {}", accountNumber);
        client.getLock(getLockKey(accountNumber)).unlock();
    }
}
