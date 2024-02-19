package com.sample.account.controller;

import com.sample.account.domain.Account;
import com.sample.account.type.AccountStatus;
import com.sample.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountController.class) //test 하려고하는 controller
class AccountControllerTest {
    @MockBean
    private AccountService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
        void successGetAccount() throws Exception{
        //given
        given(service.getAccount(anyLong()))
                .willReturn(Account.builder()
                        .accountNumber("3456")
                        .accountStatus(AccountStatus.In_USER)
                        .build());
        //when
        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print()) //get을 했을때 응답값 요청값을 화면에 보여준다.
                .andExpect(jsonPath("$.accountNumber").value("3456")) //body에 있는 첫 구조 값이
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"))
                .andExpect(status().isOk());
       }
}