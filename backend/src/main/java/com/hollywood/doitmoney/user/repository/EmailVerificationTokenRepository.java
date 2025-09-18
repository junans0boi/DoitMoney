package com.hollywood.doitmoney.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hollywood.doitmoney.user.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findTopByEmailOrderByExpiryDesc(String email);
}