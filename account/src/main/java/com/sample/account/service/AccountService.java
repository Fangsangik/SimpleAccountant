package com.sample.account.service;

import com.sample.account.domain.Account;
import com.sample.account.domain.AccountStatus;
import com.sample.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor //꼭 필요한 argument 생성자를 만들어 준다.
public class AccountService {

    private final AccountRepository repository;
    private String noFinal;

    @Transactional
    public void createAccount(){
        Account account = Account.builder()
                .accountNumber("40000")
                .accountStatus(AccountStatus.In_USER)
                .build();
        repository.save(account);
    }

    @Transactional
    public Account getAccount(Long id){
        if (id < 0) {
            throw new RuntimeException("Minus");
        }

       return repository.findById(id).get();
    }
}
