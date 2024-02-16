package practice;

import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Getter @Setter
@ToString //property 값과 value 값의 format 만들어준다
//@NoArgsConstructor 아무것도 없는 생성자를 만들어주는 기능
@AllArgsConstructor //모든 property를 갖고 있는 생성자를 형성
//@RequiredArgsConstructor 필수 값들을 받게 하는 롬복들 Spring에서 bean들을 주입받는 생성자 주입 스타일일때
@Slf4j
public class AccountDto {
   private String accountNumber;
   private String nickName;
   private LocalDateTime registeredAt;

   public void log(){
      log.info("error is occured");
   }
}
