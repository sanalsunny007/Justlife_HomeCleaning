package com.justlife.home.cleaning.repository;

import com.justlife.home.cleaning.entity.Cleaner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleanerRepository extends JpaRepository<Cleaner, Long> {
}
