package com.inimai.devjourney.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inimai.devjourney.entity.Journal;
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

        return journalRepository.save(journal);

    }
    public Journal getJournalById(Long id) {
        return journalRepository.findById(id).orElse(null);
    }


    public List<Journal> getAllJournals() {
        return journalRepository.findAll();
    }

}