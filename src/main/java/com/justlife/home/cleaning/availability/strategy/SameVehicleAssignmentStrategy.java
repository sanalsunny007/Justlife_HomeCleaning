package com.justlife.home.cleaning.availability.strategy;

import com.justlife.home.cleaning.dto.CleanerDTO;
import com.justlife.home.cleaning.exception.NoAvailableCleanersException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.justlife.home.cleaning.enums.ApplicationErrorCode.NO_VEHICLE_WITH_REQUIRED_CLEANERS;

/**
 * Strategy implementation that assigns cleaners based on the constraint:
 * All assigned cleaners must belong to the same vehicle.
 * Selected vehicle must have at least the required number of available cleaners
 */
@Slf4j
@Component
public class SameVehicleAssignmentStrategy implements CleanerAssignmentStrategy {

    /**
     * Assigns the required number of cleaners ensuring they all belong to the same vehicle.
     *
     */
    @Override
    public List<CleanerDTO> assignCleaners(List<CleanerDTO> cleaners, int requiredCount) {
        log.debug("Assigning {} cleaners from {} available", requiredCount, cleaners.size());

        Map<Long, List<CleanerDTO>> cleanersByVehicle = groupByVehicle(cleaners);

        Long selectedVehicle = findSuitableVehicle(cleanersByVehicle, requiredCount);

        List<CleanerDTO> assigned = cleanersByVehicle.get(selectedVehicle)
                .subList(0, requiredCount);

        log.info("Assigned {} cleaners from vehicle {}", requiredCount, selectedVehicle);

        return assigned;
    }

    /**
     * Groups cleaners by vehicle ID.
     */
    private Map<Long, List<CleanerDTO>> groupByVehicle(List<CleanerDTO> cleaners) {
        Map<Long, List<CleanerDTO>> grouped = cleaners.stream()
                .collect(Collectors.groupingBy(CleanerDTO::getVehicleId));

        log.debug("Grouped cleaners by {} vehicles", grouped.size());
        return grouped;
    }

    /**
     * Determines which vehicle has enough available cleaners.
     * Returns the vehicleId of the successful match.
     */
    private Long findSuitableVehicle(Map<Long, List<CleanerDTO>> grouped, int requiredCount) {
        return grouped.entrySet().stream()
                .peek(e ->
                        log.trace("Vehicle {} has {} cleaners",
                                e.getKey(), e.getValue().size()))
                .filter(e -> e.getValue().size() >= requiredCount)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("No vehicle has at least {} cleaners available", requiredCount);
                    return new NoAvailableCleanersException(NO_VEHICLE_WITH_REQUIRED_CLEANERS);
                });
    }
}

