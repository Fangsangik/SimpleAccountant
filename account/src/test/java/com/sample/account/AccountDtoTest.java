package com.sample.account;

import org.junit.jupiter.api.Test;
import practice.AccountDto;

import java.time.LocalDateTime;

class AccountDtoTest {

    @Test
        void account_test(){
        //given
        //when
        //then

        AccountDto dto = new AccountDto("accountNumber" ,
                "summer",
                LocalDateTime.now()
        );

        System.out.println(dto.getAccountNumber());
        System.out.println(dto.toString());
       }

}