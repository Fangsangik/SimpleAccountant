package com.sample.account.exception;

import com.sample.account.dto.ErrorResponse;
import com.sample.account.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.sample.account.type.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountException(AccountException e){
        log.error("{} is occurred", e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("handleMethodArgumentNotValidException has been occurred");
        return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
    }

    //Data오류가 발생했을때
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegerityViolationException(DataIntegrityViolationException e){
        log.error("handleDataIntegerityViolationException has been occurred");
        return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
    }

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleException(Exception e){
        log.error("Exception is occurred", e);

        return new ErrorResponse(INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR.getDescription());
    }
}
