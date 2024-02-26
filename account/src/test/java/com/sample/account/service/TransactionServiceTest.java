package com.sample.account.service;

import com.sample.account.domain.Account;
import com.sample.account.domain.AccountUser;
import com.sample.account.domain.Transaction;
import com.sample.account.dto.TransactionDto;
import com.sample.account.exception.AccountException;
import com.sample.account.repository.AccountRepository;
import com.sample.account.repository.AccountUserRepository;
import com.sample.account.repository.TransactionRepository;
import com.sample.account.type.AccountStatus;
import com.sample.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.sample.account.type.AccountStatus.In_USER;
import static com.sample.account.type.TransactionResultType.F;
import static com.sample.account.type.TransactionResultType.S;
import static com.sample.account.type.TransactionType.CANCEL;
import static com.sample.account.type.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest(TransactionService.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;


    @InjectMocks
    private TransactionService transactionService;

    @Test
    void successUseBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();
        user.setId(12L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(In_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapShot(9000L)
                        .build());
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        TransactionDto transactionDto = transactionService
                .useBalance(1L, "100000000", 1000L);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(9000L, captor.getValue().getBalanceSnapShot());
        assertEquals(S, transactionDto.getTransactionResultType());
        assertEquals(USE, transactionDto.getTransactionType());
        assertEquals(9000L, transactionDto.getBalanceSnapShot());
        assertEquals(1000L, transactionDto.getAmount());
    }

    @Test
    @DisplayName("해당 유저 없음 - 잔액 사용 실패")
    void deleteAccount_userNotFound() {

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty()); //여기서 에러가 터져서 밑에 로직은 실행X

        //when
        AccountException accountException = assertThrows(AccountException.class, () ->
                transactionService.useBalance(1L, "10000000000", 1000L));
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

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService.useBalance(1L, "10000000000", 1000L));

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
                -> transactionService.useBalance(1L, "10000000000", 1000L));

        assertEquals(ErrorCode.USER_ACCOUNT_UNMATCH, exception.getErrorCode());
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
                        .balance(0L)
                        .build()));

        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService.useBalance(1L, "10000000000", 1000L));

        assertEquals(ErrorCode.ACCOUNT_ALREADY_UN_REGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("거래 금액이 차액 보다 큰 경우")
    void exceedAmount_UseBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();

        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(In_USER)
                .balance(100L)
                .accountNumber("1000000012")
                .build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        //then
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService.useBalance(1L, "10000000000", 1000L));

        assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, exception.getErrorCode());
        verify(transactionRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("실패 트랜잭션 저장 성공")
    void saveFailedUseTransaction() {
        //given
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();
        user.setId(12L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(In_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapShot(9000L)
                        .build());
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        transactionService
                .saveFailedUseTransaction("100000000", 1000L);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(100000L, captor.getValue().getBalanceSnapShot());
        assertEquals(F, captor.getValue().getTransactionResultType());
    }

    @Test
    void successCancelBalance() {
        //given
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();

        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(In_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(1000L)
                .balanceSnapShot(9000L)
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));


        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionResultType(S)
                        .transactionId("transactionIdForCancel")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapShot(10000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        TransactionDto transactionDto = transactionService
                .cancelBalance("transactionId", "100000000", 1000L);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(10000L + 1000L, captor.getValue().getBalanceSnapShot());
        assertEquals(S, transactionDto.getTransactionResultType());
        assertEquals(CANCEL, transactionDto.getTransactionType());
        assertEquals(10000L, transactionDto.getBalanceSnapShot());
        assertEquals(1000L, transactionDto.getAmount());
    }

    @Test
    @DisplayName("해당 계좌 없음 - 잔액사용 취소 실패")
    void CancelAccount_AccountNotFound() {
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder()
                        .build()));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService.cancelBalance("transactionId", "10000000000", 1000L));

        //then

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("해당 거래 없음 - 잔액사용 취소 실패")
    void CancelAccount_TransactionNotFound() {
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder()
                        .build()));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService.cancelBalance("transactionId", "10000000000", 1000L));

        //then

        assertEquals(ErrorCode.Transaction_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("거래와 계좌가 매칭 X - 잔액사용 취소 실패")
    void cancelAccount_TransactionAccountUnMatch() {
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(In_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();
        account.setId(1L);

        Account accountNotInUse = Account.builder()
                .accountUser(user)
                .accountStatus(In_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();
        accountNotInUse.setId(2L);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(1000L)
                .balanceSnapShot(9000L)
                .build();

        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(accountNotInUse));

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService
                .cancelBalance("transactionId", "10000000000", 1000L));

        //then

        assertEquals(ErrorCode.TRANSACTION_ACCOUNT_UNMATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("거래와 취소 금액 X - 잔액사용 취소 실패")
    void cancelAccount_CancelMustFully() {
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(In_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();
        account.setId(1L);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(1000L + 1000L)
                .balanceSnapShot(9000L)
                .build();

        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService
                .cancelBalance("transactionId", "10000000000", 1000L));

        //then

        assertEquals(ErrorCode.CANCEL_MUST_FULLY, exception.getErrorCode());
    }

    @Test
    @DisplayName("취소는 1년까지만 가능 - 잔액사용 취소 실패")
    void cancelAccount_TooOldOrder() {
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(In_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();
        account.setId(1L);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1))
                .amount(1000L)
                .balanceSnapShot(9000L)
                .build();

        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService
                .cancelBalance("transactionId", "10000000000", 1000L));

        //then

        assertEquals(ErrorCode.TOO_OLD_ORDER_TO_CANCEL, exception.getErrorCode());
    }

    @Test
    void successQueryTransaction() {
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(In_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();
        account.setId(1L);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1))
                .amount(1000L)
                .balanceSnapShot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        TransactionDto transactionDto = transactionService.queryTransaction("trxId");
        assertEquals(USE, transactionDto.getTransactionType());
        assertEquals(S, transactionDto.getTransactionResultType());
        assertEquals(1000L, transactionDto.getAmount());
        assertEquals("transactionId", transactionDto.getTransactionId());
    }

    @Test
    @DisplayName("원 거래 없음 - 거래 조회 실패")
    void queryTransaction_TransactionNotFound() {
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService.queryTransaction("transactionId"));

        //then

        assertEquals(ErrorCode.Transaction_NOT_FOUND, exception.getErrorCode());
    }
}