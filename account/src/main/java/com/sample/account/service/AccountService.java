package com.sample.account.service;

import com.sample.account.domain.Account;
import com.sample.account.domain.AccountUser;
import com.sample.account.exception.AccountException;
import com.sample.account.repository.AccountRepository;
import com.sample.account.repository.AccountUserRepository;
import com.sample.account.type.AccountStatus;
import com.sample.account.type.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor //꼭 필요한 argument 생성자를 만들어 준다.
public class AccountService {

    private final AccountRepository repository;
    private final AccountUserRepository accountUserRepository;
    private String noFinal;

    /**
     * 사용자가 있는지 확인
     * 계좌에 번호를 생성하고
     * 계좌를 저장하고 그 정보를 넘긴다.
     */
    @Transactional
    public Account createAccount(Long userId, Long initialBalance) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        String newAccountNumber = accountUserRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("100000000");

        Account savedAccount = repository.save(Account
                .builder()
                .accountUser(accountUser)
                .accountStatus(AccountStatus.In_USER)
                .accountNumber(newAccountNumber)
                .balance(initialBalance)
                .registeredAt(LocalDateTime.now())
                .build()
        );

        return savedAccount;
    }

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("Minus");
        }

        return repository.findById(id).get();
    }
}
