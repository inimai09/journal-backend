package com.inimai.devjourney.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.inimai.devjourney.dto.LoginRequest;
import com.inimai.devjourney.entity.User;
import com.inimai.devjourney.exception.ResourceNotFoundException;
import com.inimai.devjourney.repository.UserRepository;
import com.inimai.devjourney.security.JwtUtil;
import com.inimai.devjourney.dto.LoginResponse;
import com.inimai.devjourney.dto.RegisterRequest;
import com.inimai.devjourney.dto.UserResponse;

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

    public UserResponse saveUser(RegisterRequest request) {

        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return mapToResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> response = new ArrayList<>();
        for(User user : users) {
            response.add(mapToResponse(user));
        }
        return response;
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
        .orElseThrow(() ->
            new ResourceNotFoundException("User not found"));
        
        return mapToResponse(user);
    }
    /* 
    public UserResponse updateUser(Long id, RegisterRequest request) {

        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!existingUser.getEmail().equals(request.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));

        return mapToResponse(userRepository.save(existingUser));
    }*/

    public LoginResponse login(LoginRequest request) {

    User user = userRepository
            .findByEmail(request.getEmail())
            .orElse(null);

        if (user == null) {
            return new LoginResponse(null, "Invalid credentials");
        }

        boolean matches = passwordEncoder.matches(
            request.getPassword(),
            user.getPassword()
        );

        if (!matches) {
            return new LoginResponse(null, "Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token, "Login successful");
    }
    private UserResponse mapToResponse(User user){
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }
}
