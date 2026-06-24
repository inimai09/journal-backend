package com.inimai.devjourney.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inimai.devjourney.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
