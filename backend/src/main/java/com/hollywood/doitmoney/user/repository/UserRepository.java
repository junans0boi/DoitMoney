// File: Planary_Backend/src/main/java/com/Planairy/backend/repository/UserRepository.java
package com.hollywood.doitmoney.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hollywood.doitmoney.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone); // ⚡

    boolean existsByEmail(String email); // ⚡ 실시간 중복 검사용

    boolean existsByPhone(String phone); // ⚡
}