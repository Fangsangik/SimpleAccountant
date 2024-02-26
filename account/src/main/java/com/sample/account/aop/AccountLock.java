package com.sample.account.aop;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited //상속 가능한 구조로 사용
public @interface AccountLock {
    long tryLockTime() default 5000L;
}
