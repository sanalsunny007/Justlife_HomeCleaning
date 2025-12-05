package com.justlife.home.cleaning.availability.strategy;

import com.justlife.home.cleaning.dto.CleanerDTO;
import com.justlife.home.cleaning.exception.NoAvailableCleanersException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.justlife.home.cleaning.enums.ApplicationErrorCode.NO_VEHICLE_WITH_REQUIRED_CLEANERS;
import static org.junit.jupiter.api.Assertions.*;

class SameVehicleAssignmentStrategyTest {

    private CleanerAssignmentStrategy strategy;

    @BeforeEach
    void setup() {
        strategy = new SameVehicleAssignmentStrategy();
    }

    private CleanerDTO cleaner(long id, long vehicleId) {
        return CleanerDTO.builder()
                .cleanerId(id)
                .cleanerName("C-" + id)
                .vehicleId(vehicleId)
                .vehicleName("V-" + vehicleId)
                .build();
    }

    @Test
    void assignCleaners_shouldAssignFromVehicleWithEnoughCleaners() {
        List<CleanerDTO> cleaners = List.of(
                cleaner(1, 10),
                cleaner(2, 10),
                cleaner(3, 20)
        );

        List<CleanerDTO> assigned = strategy.assignCleaners(cleaners, 2);

        assertEquals(2, assigned.size());
        assertEquals(10L, assigned.get(0).getVehicleId());
    }

    @Test
    void assignCleaners_shouldChooseCorrectVehicleWhenMultipleVehiclesExist() {
        List<CleanerDTO> cleaners = List.of(
                cleaner(1, 5),
                cleaner(2, 5),
                cleaner(3, 7),
                cleaner(4, 7),
                cleaner(5, 7)
        );

        List<CleanerDTO> assigned = strategy.assignCleaners(cleaners, 3);

        assertEquals(3, assigned.size());
        assertTrue(assigned.stream().allMatch(c -> c.getVehicleId() == 7));
    }

    @Test
    void assignCleaners_shouldThrowException_whenNoVehicleHasEnoughCleaners() {
        List<CleanerDTO> cleaners = List.of(
                cleaner(1, 10),
                cleaner(2, 20),
                cleaner(3, 30)
        );

        NoAvailableCleanersException ex = assertThrows(
                NoAvailableCleanersException.class,
                () -> strategy.assignCleaners(cleaners, 2)
        );

        assertEquals(NO_VEHICLE_WITH_REQUIRED_CLEANERS.getCode(), ex.getErrorCode());
    }

    @Test
    void assignCleaners_shouldWorkWhenVehicleHasExactRequiredCount() {
        List<CleanerDTO> cleaners = List.of(
                cleaner(1, 100),
                cleaner(2, 100)
        );

        List<CleanerDTO> assigned = strategy.assignCleaners(cleaners, 2);

        assertEquals(2, assigned.size());
        assertTrue(assigned.stream().allMatch(c -> c.getVehicleId() == 100));
    }

    @Test
    void assignCleaners_shouldReturnFirstMatchingVehicle_whenMultipleVehiclesQualify() {
        List<CleanerDTO> cleaners = List.of(
                cleaner(1, 50),
                cleaner(2, 50),
                cleaner(3, 60),
                cleaner(4, 60),
                cleaner(5, 60)
        );

        // Both vehicle 50 and 60 have >=2 cleaners → expect first match (vehicle 50)
        List<CleanerDTO> assigned = strategy.assignCleaners(cleaners, 2);

        assertEquals(2, assigned.size());
        assertTrue(assigned.stream().allMatch(c -> c.getVehicleId() == 50));
    }
}
