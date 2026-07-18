package com.inimai.devjourney.controller;

import java.util.*;
import com.inimai.devjourney.dto.LoginRequest;
import com.inimai.devjourney.dto.LoginResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inimai.devjourney.dto.RegisterRequest;
import com.inimai.devjourney.dto.UserResponse;
import com.inimai.devjourney.service.UserService;

import jakarta.validation.Valid;
//it talks to frontend
@RestController
@RequestMapping("/users")//all urls starts with /users lol
public class UserController {

    private final UserService userservice;

    public UserController(UserService userservice) {
        this.userservice = userservice;
    }

    @PostMapping
    //to create a new user, iuse @RequestBody to bind json to regreq
    public UserResponse createUser(@Valid @RequestBody RegisterRequest request) {
        return userservice.saveUser(request);
    }
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userservice.getAllUsers();
    }
    @GetMapping("/{id}")
    //to get user by id,i use @PathVariable to extract the id from the URL 
    public UserResponse getUserById(@PathVariable Long id) {
        return userservice.getUserById(id);
    }
    @PostMapping("/login")
    //LOGINRESPONSE is dto it goes back to frontend, it gets the values the backend gives
    //and requestbody loginreq is a dto in which the frontend communicates through to backend, it has json values
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userservice.login(request);
    } 
}