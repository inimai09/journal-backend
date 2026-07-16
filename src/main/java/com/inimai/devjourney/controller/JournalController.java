package com.inimai.devjourney.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inimai.devjourney.entity.Journal;
import com.inimai.devjourney.service.JournalService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/journals") 
public class JournalController {
    
    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @PostMapping
    public Journal createJournal(@RequestBody Journal journal) {
        return journalService.saveJournal(journal);
    }
    @GetMapping
    public List<Journal> getAllJournals() {
        return journalService.getAllJournals();
    }
    @GetMapping("/{id}")
    public Journal getJournal(@PathVariable Long id){
        return journalService.getJournalById(id);
    }
    @PutMapping("/{id}")
    public Journal updateJournal(@PathVariable Long id, @RequestBody Journal journal) {
           return journalService.updateJournal(id, journal);
    }
    @DeleteMapping("/{id}")
    public String deleteJournal(@PathVariable Long id) {
        return journalService.deleteJournal(id);
    }
    
}
