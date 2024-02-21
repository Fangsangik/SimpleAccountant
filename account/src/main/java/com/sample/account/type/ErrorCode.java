package com.sample.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //code별로 정보를 넣어주면 도움이 된다.
    USER_NOT_FOUND("사용자가 없습니다"),
    MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌는 10개 입니다");

    private final String description;
}
