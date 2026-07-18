package com.inimai.devjourney.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inimai.devjourney.dto.JournalRequest;
import com.inimai.devjourney.dto.JournalResponse;
import com.inimai.devjourney.service.JournalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/journals")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @PostMapping
    public JournalResponse createJournal(@Valid @RequestBody JournalRequest request) {
        return journalService.saveJournal(request);
    }

    @GetMapping
    public List<JournalResponse> getAllJournals() {
        return journalService.getAllJournals();
    }

    @GetMapping("/{id}")
    public JournalResponse getJournal(@PathVariable Long id) {
        return journalService.getJournalById(id);
    }

    @PutMapping("/{id}")
    public JournalResponse updateJournal(
            @PathVariable Long id,
            @Valid @RequestBody JournalRequest request) {

        return journalService.updateJournal(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteJournal(@PathVariable Long id) {
        return journalService.deleteJournal(id);
    }
}