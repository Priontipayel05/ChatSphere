package com.chatsphere.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatsphere.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsernameKey(String usernameKey);
    boolean existsByUsernameKey(String usernameKey);
}
