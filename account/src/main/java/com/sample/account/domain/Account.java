package com.sample.account.domain;

import com.sample.account.exception.AccountException;
import com.sample.account.type.AccountStatus;
import com.sample.account.type.ErrorCode;
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
public class Account extends BaseEntity{

    @ManyToOne
    private AccountUser accountUser;

    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    //enum은 실제로는 0,1,2,3 ->
    //Enumerated -> String 실제 이름을 DB에 저장
    private AccountStatus accountStatus;

    public void useBalance(Long amount){
        if (amount > balance){
            throw  new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        } else {
            balance -= amount;
        }
    }

    public void cancelBalance(Long amount){
        if (amount < 0){
            throw  new AccountException(ErrorCode.INVALID_REQUEST);
        } else {
            balance += amount;
        }
    }

}
