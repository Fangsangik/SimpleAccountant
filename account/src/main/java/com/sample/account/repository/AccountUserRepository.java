package com.sample.account.repository;

import com.sample.account.domain.Account;
import com.sample.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
}
