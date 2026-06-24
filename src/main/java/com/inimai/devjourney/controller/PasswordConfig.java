package com.inimai.devjourney.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordConfig {
    //bean it encodes pass and returns to userserv
    @Bean
    //password encoder encoder = new BCryptPasswordEncoder(); aa
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
