package com.hollywood.doitmoney.security;

import com.hollywood.doitmoney.user.entity.User;
import com.hollywood.doitmoney.user.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("등록된 사용자가 없습니다: " + email));
        return new CustomUserDetails(u);
    }
}