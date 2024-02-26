package com.sample.account.service;

import com.sample.account.domain.Account;
import com.sample.account.domain.AccountUser;
import com.sample.account.exception.AccountException;
import com.sample.account.repository.AccountRepository;
import com.sample.account.repository.AccountUserRepository;
import com.sample.account.type.AccountStatus;
import com.sample.account.type.ErrorCode;
import com.sample.account.dto.AccountDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest1 {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountSuccess() {
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("100000012")
                        .build()));

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("100000013")
                        .build());

        //결과가 어떡해 나왔는지 보기 위함
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("100000013", captor.getValue().getAccountNumber());
    }

    @Test
    void createFirstAccountSuccess() {
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("100000013")
                        .build());

        //결과가 어떡해 나왔는지 보기 위함
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("100000000", captor.getValue().getAccountNumber());
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void createAccount_userNotFound() {
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty()); //여기서 에러가 터져서 밑에 로직은 실행X

        //when
        AccountException accountException = assertThrows(AccountException.class, () ->
                accountService.createAccount(1L, 10000L));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    @DisplayName("유저 당 최대 계좌는 10개")
    void createAccount_maxAccountIs10() {
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.countByAccountUser(any()))
                .willReturn(11);
        //when
        AccountException accountException = assertThrows(AccountException.class, () ->
                accountService.createAccount(1L, 10000L));
        //then
        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, accountException.getErrorCode());
    }

    @Test
    void deleteAccountService() {
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder().
                        accountUser(user).accountNumber("1000000012").balance(0L).
                        build()));

        //결과가 어떡해 나왔는지 보기 위함
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto accountDto = accountService.deleteAccount(1L, "1234567890");

        //then
        verify(accountRepository, times(1)).save(any());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("100000000", captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 해지 실패")
    void deleteAccount_userNotFound() {

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty()); //여기서 에러가 터져서 밑에 로직은 실행X

        //when
        AccountException accountException = assertThrows(AccountException.class, () ->
                accountService.deleteAccount(1L, "1234567890"));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 해지 실패_해당 계좌 없음")
    void deleteAccount_AccountNotFound() {
        AccountUser user = AccountUser.builder()
                .name("Pobi")
                .build();
        user.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //결과가 어떡해 나왔는지 보기 위함
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> accountService.deleteAccount(1L, "1234567890"));

        //then

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 소유주 다름")
    void deleteAccountFailed_userUnMatch() {
        AccountUser Pobi = AccountUser.builder()
                .name("Pobi")
                .build();
        Pobi.setId(12L);

        AccountUser Harry = AccountUser.builder()
                .name("Harry")
                .build();
        Harry.setId(15L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder().
                        accountUser(Harry).accountNumber("1000000012").balance(0L).
                        build()));

        AccountException exception = assertThrows(AccountException.class, ()
                -> accountService.deleteAccount(1L, "1234567890"));

        assertEquals(ErrorCode.USER_ACCOUNT_UNMATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 비어 있지 않음 ")
    void balance_NotEmpty() {
        AccountUser Pobi = AccountUser.builder()
                .name("Pobi")
                .build();

        Pobi.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder().
                        accountUser(Pobi).accountNumber("1000000012").balance(100L).
                        build()));

        AccountException exception = assertThrows(AccountException.class, ()
                -> accountService.deleteAccount(1L, "1234567890"));

        assertEquals(ErrorCode.BALANCE_NOT_EMPTY, exception.getErrorCode());
    }

    @Test
    @DisplayName("해지 계좌는 해지 할 수 없다")
    void alreadyUnRegistered() {
        AccountUser Pobi = AccountUser.builder()
                .name("Pobi")
                .build();
        Pobi.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder().
                        accountUser(Pobi)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("1000000012")
                        .balance(0L).
                        build()));

        AccountException exception = assertThrows(AccountException.class, ()
                -> accountService.deleteAccount(1L, "1234567890"));

        assertEquals(ErrorCode.ACCOUNT_ALREADY_UN_REGISTERED, exception.getErrorCode());
    }

    @Test
    void successGetAccountByUserId(){
        AccountUser Pobi = AccountUser.builder()
                .name("Pobi")
                .build();
        Pobi.setId(12L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));

        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .accountUser(Pobi)
                        .accountNumber("1111111")
                        .balance(1000L)
                        .build(),
                Account.builder()
                        .accountUser(Pobi)
                        .accountNumber("222222")
                        .balance(2000L)
                        .build(),
                Account.builder()
                        .accountUser(Pobi)
                        .accountNumber("333333")
                        .balance(3000L)
                        .build()
        );
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);

        List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

        assertEquals(3, accountDtos.size());
        assertEquals("1111111", accountDtos.get(0).getAccountNumber());
        assertEquals(1000, accountDtos.get(0).getBalance());
        assertEquals("2222222", accountDtos.get(1).getAccountNumber());
        assertEquals(2000, accountDtos.get(1).getBalance());
        assertEquals("3333333", accountDtos.get(3).getAccountNumber());
        assertEquals(3000, accountDtos.get(3).getBalance());
    }

    @Test
    void failedToGetAccounts(){
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.getAccountsByUserId(1L));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}
