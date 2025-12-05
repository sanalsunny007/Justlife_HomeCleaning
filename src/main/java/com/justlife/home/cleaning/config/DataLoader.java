package com.justlife.home.cleaning.config;

import com.justlife.home.cleaning.entity.Cleaner;
import com.justlife.home.cleaning.entity.Vehicle;
import com.justlife.home.cleaning.repository.CleanerRepository;
import com.justlife.home.cleaning.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This component auto-generates initial demo data
 *  5 Vehicles ,5 Cleaners per vehicle (25 cleaners)
 *
 * WARNING:
 * Seeding or modifying database contents at application startup using a
 *  CommandLineRunner is NOT recommended for production environments.
 *
 *  In production, all database schema changes and initial reference data
 *  must be applied using proper database migration tools such as Flyway or Liquibase
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final VehicleRepository vehicleRepository;
    private final CleanerRepository cleanerRepository;

    @Override
    public void run(String... args) {
        // Prevent duplicate initialization
        if (vehicleRepository.count() > 0 || cleanerRepository.count() > 0) {
            log.info("DataLoader skipped — vehicles/cleaners already exist.");
            return;
        }

        log.info("Seeding initial data: 5 vehicles + 25 cleaners...");

        // -------------------------------------------------------------
        // Create 5 Vehicles
        // -------------------------------------------------------------
        List<Vehicle> vehicles = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            vehicles.add(
                    Vehicle.builder()
                            .name("Vehicle-" + i)
                            .build()
            );
        }

        vehicleRepository.saveAll(vehicles);

        // -------------------------------------------------------------
        // Create 5 cleaners per vehicle (total 25 cleaners)
        // -------------------------------------------------------------
        List<Cleaner> cleaners = new ArrayList<>();

        for (Vehicle v : vehicles) {
            for (int i = 1; i <= 5; i++) {
                cleaners.add(
                        Cleaner.builder()
                                .name("Cleaner-" + v.getId() + "-" + i)
                                .workStart(LocalTime.of(8, 0))
                                .workEnd(LocalTime.of(22, 0))
                                .vehicle(v)
                                .build()
                );
            }
        }

        cleanerRepository.saveAll(cleaners);

        log.info("DataLoader completed successfully. {} vehicles, {} cleaners created.",
                vehicles.size(), cleaners.size());
    }
}