package com.justlife.home.cleaning.availability.strategy;

import com.justlife.home.cleaning.dto.CleanerDTO;

import java.util.List;

public interface CleanerAssignmentStrategy {

    List<CleanerDTO> assignCleaners(List<CleanerDTO> cleaners, int requiredCount);
}
