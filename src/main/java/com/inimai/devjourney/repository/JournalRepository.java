package com.inimai.devjourney.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.inimai.devjourney.entity.Journal;

public interface JournalRepository extends JpaRepository<Journal, Long> {

}
