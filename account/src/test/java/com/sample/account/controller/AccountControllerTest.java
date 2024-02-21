package com.sample.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.account.domain.Account;
import com.sample.account.service.AccountService;
import com.sample.account.service.RedisServiceTest;
import com.sample.account.type.AccountStatus;
import dto.AccountDto;
import dto.CreateAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountController.class) //test 하려고하는 controller
    //Mocking 된 bean들이 AccountController와 함께 testController에 올라가고
    //실제 AccountController와 가짜 AccountService, 가짜 RedisServiceTest가 올라가서 합쳐지게 된다

class AccountControllerTest {
    @MockBean
    private AccountService service;

    @MockBean
    private RedisServiceTest redisServiceTest;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    //Jackson이라고 하는 object -> json , json <- object

    @Test
        void SuccessCreateAccount() throws Exception {
        //given
        given(service.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("123456789")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());
        //when
        //then
        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateAccount.Request(1L, 100L)
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1)) //json경로에 받았던
                .andExpect(jsonPath("$.accountNumber").value("123456789"))
                .andDo(print());
       }

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