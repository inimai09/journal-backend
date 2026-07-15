package com.inimai.devjourney.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.inimai.devjourney.entity.Journal;
import com.inimai.devjourney.entity.User;
import com.inimai.devjourney.repository.JournalRepository;
import com.inimai.devjourney.repository.UserRepository;

@Service
public class JournalService {

    private final JournalRepository journalRepository;
    private final UserRepository userRepository;

    public JournalService(JournalRepository journalRepository, UserRepository userRepository) {
        this.journalRepository = journalRepository;
        this.userRepository = userRepository;
    }

    public Journal saveJournal(Journal journal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        journal.setCreatedAt(LocalDateTime.now());
        journal.setUser(user);
        return journalRepository.save(journal);
    }
    //to understand authentication.whatever()
    //Authentication
    //Principal (Who?)
   //Credentials (Password)
   //Authorities (Roles)
   //Authenticated? (true/false)
    public Journal getJournalById(Long id) {
        return journalRepository.findById(id).orElse(null);
    }
    public List<Journal> getAllJournals() {
        return journalRepository.findAll();
    }
}