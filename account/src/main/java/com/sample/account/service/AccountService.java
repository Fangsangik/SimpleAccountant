package com.sample.account.service;

import com.sample.account.domain.Account;
import com.sample.account.domain.AccountUser;
import com.sample.account.exception.AccountException;
import com.sample.account.repository.AccountRepository;
import com.sample.account.repository.AccountUserRepository;
import com.sample.account.type.AccountStatus;
import com.sample.account.dto.AccountDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sample.account.type.ErrorCode.*;

@Service
@RequiredArgsConstructor //꼭 필요한 argument 생성자를 만들어 준다.
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;
    private String noFinal;

    /**
     * 사용자가 있는지 확인
     * 계좌에 번호를 생성하고
     * 계좌를 저장하고 그 정보를 넘긴다.
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        AccountUser accountUser = getAccountUser(userId);

        validateCreateAccount(accountUser);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("100000000");

        //한번만 쓰는 변수는 크게 의미가 없다.
        //호불호가 있을 수 있지만 중간에 로직이 들어갈 경우도 있기에 나중에 혼란 스럽다.
        return AccountDto.fromEntity(
                accountRepository.save
                (Account
                .builder()
                .accountUser(accountUser)
                .accountStatus(AccountStatus.In_USER)
                .accountNumber(newAccountNumber)
                .balance(initialBalance)
                .registeredAt(LocalDateTime.now())
                .build()
        ));
    }

    private AccountUser getAccountUser(Long userId) {
        return accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) >= 10){
            throw new AccountException(MAX_ACCOUNT_PER_USER_10);
        }
    }

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("Minus");
        }

        return accountRepository.findById(id).get();
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser user = getAccountUser(userId);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateDeleteAccount(user, account);
        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnregisteredAt(LocalDateTime.now());

        accountRepository.save(account); //단순히 test를 위함 (save가 있으면 혼돈을 줄 수 있다)

        return AccountDto.fromEntity(account);
    }

    private void validateDeleteAccount(AccountUser user, Account account) {
        if (!Objects.equals(user.getId(), account.getAccountUser().getId())){
            throw new AccountException(USER_ACCOUNT_UNMATCH);
        }

        if (account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new AccountException(ACCOUNT_ALREADY_UN_REGISTERED);
        }

        if (account.getBalance() > 0){
            throw new AccountException(BALANCE_NOT_EMPTY);
        }
    }

    public List<AccountDto> getAccountsByUserId(Long userId) {
        AccountUser accountUser = getAccountUser(userId);

        List<Account> accounts = accountRepository.findByAccountUser(accountUser);

        return accounts.stream().
                map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }
}
