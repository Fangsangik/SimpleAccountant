package com.sample.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //code별로 정보를 넣어주면 도움이 된다.
    USER_NOT_FOUND("사용자가 없습니다");

    private final String description;
}
