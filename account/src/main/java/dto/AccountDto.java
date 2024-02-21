package dto;

import com.sample.account.domain.Account;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//앤티티 클레스와 비슷하지만 필요한 것만 넣어 놓는 것
public class AccountDto {
    private Long userId;
    private String accountNumber;
    private Long balance;
    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

//    public AccountDto(Long userId) {
//        this.userId = userId;
//    }

    //특정 타입에서 특정 타입으로 변경해줄 때 (엔티티를 통해서 만들어지는 경무가 많다.)
    //엔티티를 갖고 static한 메서드를 만들어 주는 것을 추천
    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .userId(account.getAccountUser().getId())
                .accountNumber(account.getAccountNumber())
                .registeredAt(account.getRegisteredAt())
                .unRegisteredAt(account.getUnregisteredAt())
                .build();
    }
}
