package com.inimai.devjourney.controller;

import java.util.*;
import com.inimai.devjourney.dto.LoginRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inimai.devjourney.dto.RegisterRequest;
import com.inimai.devjourney.entity.User;
import com.inimai.devjourney.service.UserService;
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
    public User createUser(@RequestBody RegisterRequest request) {

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return userservice.saveUser(user);
    }
    @GetMapping
    public List<User> getAllUsers() {
        return userservice.getAllUsers();
    }
    @GetMapping("/{id}")
    //to get user by id,i use @PathVariable to extract the id from the URL 
    public User getUserById(@PathVariable Long id) {
        return userservice.getUserById(id);
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return userservice.login(request);
    }
}