package com.justlife.home.cleaning.service;

import com.justlife.home.cleaning.dto.CleanerAvailabilityDTO;
import com.justlife.home.cleaning.dto.CleanerDTO;
import com.justlife.home.cleaning.entity.Cleaner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AvailabilityService {
    List<CleanerAvailabilityDTO> getAvailabilityForDate(LocalDate date);

    List<CleanerDTO> getAvailableCleaners(LocalDate date, LocalTime startTime, int durationHours);
}
