package com.sample.account.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

//client와 controller 주고 받는 관계
public class AccountInfo {
    private String accountNumber;
    private Long balance;


}
