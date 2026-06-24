package com.inimai.devjourney.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.inimai.devjourney.dto.LoginRequest;
import com.inimai.devjourney.entity.User;
import com.inimai.devjourney.repository.UserRepository;
import com.inimai.devjourney.security.JwtUtil;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
            .orElse(null);

        if (existingUser == null) {
           return null;
        }

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        return userRepository.save(existingUser);
    }

    public String login(LoginRequest request) {

    User user = userRepository
            .findByEmail(request.getEmail())
            .orElse(null);

        if (user == null) {
            return "User not found";
        }

        boolean matches = passwordEncoder.matches(
            request.getPassword(),
            user.getPassword()
        );

        if (!matches) {
            return "Invalid password";
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return "Login successful. Token: " + token;
    }
}
