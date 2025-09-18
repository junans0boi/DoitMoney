/* repository/PasswordResetTokenRepository.java */
package com.hollywood.doitmoney.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hollywood.doitmoney.user.entity.PasswordResetToken;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}