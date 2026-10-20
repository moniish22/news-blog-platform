package com.newsblog.repository;

import com.newsblog.model.Role;
import com.newsblog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByResetToken(String token);
    long countByRole(Role role);
}
