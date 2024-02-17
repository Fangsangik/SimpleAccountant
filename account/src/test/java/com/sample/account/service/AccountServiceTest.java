package com.sample.account.service;

import com.sample.account.domain.Account;
import com.sample.account.domain.AccountStatus;
import com.sample.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//@SpringBootTest 환경 맥락 등 bean들을 등록
@ExtendWith(MockitoExtension.class) //확장팩 클래스를 달아준다.
class AccountServiceTest {

    //    @Autowired
    @Mock //가짜로 만들어줘서 mock으로 만들어 줘서 의존성과 비슷하게 생성을 해준다.
    private AccountRepository repository;

    @InjectMocks
    private AccountService service;

    @BeforeEach
    public void before() {
        service.createAccount();
    }

    @Test
    @DisplayName("계좌 조회 성공")
    void testXXX() {
        //given
        given(repository.findById(anyLong())).
                willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("Korea")
                        .build()
                ));

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class); //long type의 box를 형성

        //when
        Account account = service.getAccount(44444L);

        //then
        //getAccount 했을때 accountRepository가 findById를 한번 부름
        verify(repository, times(1)).findById(captor.capture()); //captor를 가로채겠다
        //accountRepository가 저장을 하면 안된다.
        verify(repository, times(0)).save(any());
        assertEquals(4555L, captor.getValue()); //가져야 할 value가 4555여야 한다,
        assertNotEquals(45551L, captor.getValue());
        assertEquals("Korea", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    @DisplayName("계좌 조회 실패 음수로 조회")
    void testFail() {
        //given

        //when
        //나온 결과값을 말하는 게 아닌 이런 동작을 했을때 이런 결과 값이 나올 것이다.
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.getAccount(-10L));

        //then
        assertEquals("Minus", exception.getMessage());
    }


    @Test
    @DisplayName("계좌 조회 성공")
    void testXXX1() {
        //given
        given(repository.findById(anyLong())).
                willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("Korea")
                        .build()
                ));


        //when
        Account account = service.getAccount(44444L);

        //then
        assertEquals("Korea", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }

    @Test
    @DisplayName("계좌 조회 성공")
    void testXXX2() {
        //given
        given(repository.findById(anyLong())).
                willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("Korea")
                        .build()
                ));

        //when
        Account account = service.getAccount(44444L);

        //then
        assertEquals("Korea", account.getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, account.getAccountStatus());
    }
}