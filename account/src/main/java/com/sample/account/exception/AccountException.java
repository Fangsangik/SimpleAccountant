package com.sample.account.exception;

import com.sample.account.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountException extends RuntimeException {
    //Exception을 그냥 extends를 하게 되면 메서드 시그니처에 exception을 줄줄이 붙이고 다녀야 하는 불편함
    //checked Exception은 transaction을 rollback 해주는 대상이 되지 않는다.

    private ErrorCode errorCode;
    private String errorMessage;

    public AccountException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
