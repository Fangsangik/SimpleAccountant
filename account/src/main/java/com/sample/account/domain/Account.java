package com.sample.account.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Account {

    @Id //pk 설정
    @GeneratedValue
    private Long id;


    private String accountNumber;

    @Enumerated(EnumType.STRING)
    //enum은 실제로는 0,1,2,3 ->
    //Enumerated -> String 실제 이름을 DB에 저장
    private AccountStatus accountStatus;

}
