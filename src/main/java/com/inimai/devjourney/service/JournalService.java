package com.inimai.devjourney.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.inimai.devjourney.entity.Journal;
import com.inimai.devjourney.entity.User;
import com.inimai.devjourney.repository.JournalRepository;

@Service
public class JournalService {

    private final JournalRepository journalRepository;

    public JournalService(JournalRepository journalRepository) {
        this.journalRepository = journalRepository;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return journalRepository.findByIdAndUser(id, user)
                .orElse(null);
    }
    public List<Journal> getAllJournals() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        return journalRepository.findByUser(user);
    }

    public Journal updateJournal(Long id, Journal journal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Journal j = journalRepository.findByIdAndUser(id, user).orElse(null);

        if(j == null){
            return null;
        }
        j.setTitle(journal.getTitle());
        j.setContent(journal.getContent());
        return journalRepository.save(j);//exisiting journal obj is updated with new title and content
    }
    public String deleteJournal(Long id) {
    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    User user = (User) authentication.getPrincipal();
    Journal j = journalRepository
            .findByIdAndUser(id, user)
            .orElse(null);

    if (j == null) {
        return "Journal not found";
    }
    journalRepository.delete(j);
    return "Journal deleted successfully";
    }
}