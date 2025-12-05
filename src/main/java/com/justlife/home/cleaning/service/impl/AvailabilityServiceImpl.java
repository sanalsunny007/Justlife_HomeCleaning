package com.justlife.home.cleaning.service.impl;

import com.justlife.home.cleaning.availability.rules.CleanerAvailabilityRule;
import com.justlife.home.cleaning.availability.windows.FreeWindowCalculator;
import com.justlife.home.cleaning.dto.CleanerAvailabilityDTO;
import com.justlife.home.cleaning.dto.CleanerDTO;
import com.justlife.home.cleaning.dto.TimeWindowDTO;
import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;
import com.justlife.home.cleaning.enums.ApplicationErrorCode;
import com.justlife.home.cleaning.enums.BookingErrorCode;
import com.justlife.home.cleaning.exception.BookingValidationException;
import com.justlife.home.cleaning.repository.BookingRepository;
import com.justlife.home.cleaning.repository.CleanerRepository;
import com.justlife.home.cleaning.service.AvailabilityService;
import com.justlife.home.cleaning.utils.CleanerMapper;
import com.justlife.home.cleaning.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {

    private final CleanerRepository cleanerRepository;
    private final BookingRepository bookingRepository;

    //rules are interchangeable
    private final CleanerAvailabilityRule cleanerAvailabilityRule;
    private final FreeWindowCalculator freeWindowCalculator;

    /**
     * Returns detailed availability windows for each cleaner.
     */
    @Override
    public List<CleanerAvailabilityDTO> getAvailabilityForDate(LocalDate date) {
        log.info("Checking availability for date {}", date);


        if (!isDateAndTimeValid(date)) {
            return List.of();
        }

        List<Cleaner> cleaners = cleanerRepository.findAll();
        List<Booking> bookings = bookingRepository.findByDate(date);

        return cleaners.stream()
                .map(cleaner -> buildCleanerAvailability(cleaner, date, bookings))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Finds all cleaners available for a given time window.
     *
     */
    @Override
    public List<CleanerDTO> getAvailableCleaners(LocalDate date, LocalTime startTime, int durationHours) {
        log.info("Checking available cleaners for {} at {} for {}h",
                date, startTime, durationHours);

        LocalDateTime start = LocalDateTime.of(date, startTime);
        LocalDateTime end = start.plusHours(durationHours);

        if (!isDateAndTimeValid(date, start, end)) {
            return List.of();
        }

        return cleanerRepository.findAll().stream()
                .filter(cleaner -> isCleanerAvailable(cleaner, date, start, end))
                .map(CleanerMapper::toResponse)
                .toList();
    }

    /**
     * This method is responsible for assembling the availability view of one cleaner,
     * including all free time windows during the working day
     */
    private CleanerAvailabilityDTO buildCleanerAvailability(Cleaner cleaner,
                                                            LocalDate date,
                                                            List<Booking> allBookings) {

        List<Booking> bookingsForCleaner = allBookings.stream()
                .filter(b -> b.getCleaners().contains(cleaner))
                .toList();

        List<TimeWindowDTO> freeWindows =
                freeWindowCalculator.computeFreeWindows(cleaner, date, bookingsForCleaner);

        if (freeWindows.isEmpty()) {
            return null;
        }

        return CleanerAvailabilityDTO.builder()
                .cleanerId(cleaner.getId())
                .cleanerName(cleaner.getName())
                .vehicleId(cleaner.getVehicle().getId())
                .vehicleName(cleaner.getVehicle().getName())
                .availableWindows(freeWindows)
                .build();
    }

    /**
     * Determines whether a cleaner is available to take a new booking for a given
     * date and time interval.
     *
     */
    private boolean isCleanerAvailable(Cleaner cleaner,
                                       LocalDate date,
                                       LocalDateTime start,
                                       LocalDateTime end) {

        List<Booking> bookings = bookingRepository.findByCleanerAndDate(cleaner, date);

        return cleanerAvailabilityRule.isCleanerAvailable(cleaner, date, start, end, bookings);
    }


    /**
     * Validates whether a given booking date and time range is allowed
     * based on business rules:
     */
    private boolean isDateAndTimeValid(LocalDate date, LocalDateTime start, LocalDateTime end) {

        if (ValidationUtils.isPastDate(date)) {
            log.warn("Validation failed: date {} is in the past", date);
            throw new BookingValidationException(ApplicationErrorCode.PAST_DATE_NOT_ALLOWED);
        }

        if (ValidationUtils.isNonWorkingDay(date)) {
            log.warn("Validation failed: {} is a non-working day", date);
            return false;
        }

        // If only date is provided  stop here
        if (start == null || end == null) {
            return true;
        }

        if (ValidationUtils.isOutsideWorkingHours(start, end)) {
            log.warn("Validation failed: time window {} - {} is outside working hours", start, end);
            return false;
        }

        long hours = Duration.between(start, end).toHours();
        if (!ValidationUtils.isValidDuration(hours)) {
            log.error("Invalid duration: {} hours (allowed: 2 or 4)", hours);
            throw new BookingValidationException(BookingErrorCode.INVALID_DURATION);
        }

        return true;
    }

    /**
     * Shortcut for date-only validation.
     */
    private boolean isDateAndTimeValid(LocalDate date) {
        return isDateAndTimeValid(date, null, null);
    }

}
