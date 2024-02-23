package com.sample.account.repository;

import com.sample.account.domain.Account;
import com.sample.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
//Account -> JPA가 활용하게될 Repositroy
//Long -> JPA primary Key
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findFirstByOrderByIdDesc();
    Integer countByAccountUser(AccountUser accountUser); //account 안에 accountUser를 연관관계로 들고 있기 때문에 가능하다.

    Optional<Account> findByAccountNumber(String AccountNumber);

    List<Account> findByAccountUser(AccountUser accountUser);
}
