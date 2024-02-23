package com.sample.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //code별로 정보를 넣어주면 도움이 된다.
    USER_NOT_FOUND("사용자가 없습니다"),
    MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌는 10개 입니다"),
    ACCOUNT_NOT_FOUND("일치하는 계좌가 없습니다"),
    ACCOUNT_ALREADY_UN_REGISTERED("이미 계좌가 해지 되었습니다"),
    BALANCE_NOT_EMPTY("계좌가 비어 있지 않습니다."),
    USER_ACCOUNT_UNMATCH("사용자와 계좌의 소유주가 다릅니다");

    private final String description;
}
