package com.sample.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //code별로 정보를 넣어주면 도움이 된다.
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다"),
    ACCOUNT_TRANSACTION_LOCK("해당 계좌는 사용중입니다"),
    USER_NOT_FOUND("사용자가 없습니다"),
    INVALID_REQUEST("잘못된 요청입니다"),
    MAX_ACCOUNT_PER_USER_10("사용자 최대 계좌는 10개 입니다"),
    ACCOUNT_NOT_FOUND("일치하는 계좌가 없습니다"),
    ACCOUNT_ALREADY_UN_REGISTERED("이미 계좌가 해지 되었습니다"),
    BALANCE_NOT_EMPTY("계좌가 비어 있지 않습니다."),
    AMOUNT_EXCEED_BALANCE("거래 금액이 계좌 잔액 보다 큽니다."),
    TOO_OLD_ORDER_TO_CANCEL("1년이 지난 거래는 취소가 불가능 합니다."),
    CANCEL_MUST_FULLY("부분 취소는 허용되지 않습니다."),
    Transaction_NOT_FOUND("해당 거래가 없습니다"),
    USER_ACCOUNT_UNMATCH("사용자와 계좌의 소유주가 다릅니다"),
    TRANSACTION_ACCOUNT_UNMATCH("이 거래는 해당 계좌에서 발생한 거래가 아닙니다.");

    private final String description;
}
