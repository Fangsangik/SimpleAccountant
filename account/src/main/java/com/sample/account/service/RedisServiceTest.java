package com.sample.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceTest {
    private final RedissonClient client;

    public String getLock(){
        RLock lock = client.getLock("sampleLock");

        try {
            boolean isLock = lock.tryLock(1, 3, TimeUnit.SECONDS);
            //시도는 최대 1초 동안 시도,
            //lock을 획득을 하면 3초 동안 소유 하고 있다가, 풀어줌
            //unlock을 해주지 않았기 때문에 다른게 lock을 소유 하려고 하면 실패

            if (!isLock){ //lock 획득에 실패
                log.error("=============");
                return "lock failed";
            }

        } catch (Exception e){
            log.error("lock failed");
        }

        return "get Lock Success";
    }
}
