package com.akshayan.authservice.repository;

import com.akshayan.authservice.model.ForumUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForumUserRepository extends JpaRepository<ForumUser, Long> {
    Optional<ForumUser> findByUsername(String username);
    Optional<ForumUser> findByUserId(Long userId);
    ForumUser findByEmail(String email);
}
