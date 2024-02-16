package com.sample.account.repository;

import com.sample.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//Account -> JPA가 활용하게될 Repositroy
//Long -> JPA primary Key
public interface AccountRepository extends JpaRepository<Account, Long> {
}
