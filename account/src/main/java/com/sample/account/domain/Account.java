package com.sample.account.domain;

import com.sample.account.type.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Account {

    @Id //pk 설정
    @GeneratedValue
    private Long id;

    @ManyToOne
    private AccountUser accountUser;

    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    private String accountNumber;

    @Enumerated(EnumType.STRING)
    //enum은 실제로는 0,1,2,3 ->
    //Enumerated -> String 실제 이름을 DB에 저장
    private AccountStatus accountStatus;

}
