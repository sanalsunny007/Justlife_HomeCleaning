package com.justlife.home.cleaning.availability.windows;

import com.justlife.home.cleaning.constants.ApplicationConstants;
import com.justlife.home.cleaning.dto.TimeWindowDTO;
import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Calculates available time windows for a cleaner based on working hours,existing bookings
 * and Mandatory break between booking
 * The algorithm walks through the cleaner's day using a moving pointer
 * and calculates gaps between confirmed bookings.
 */
@Slf4j
@Component
public class FreeWindowCalculatorImpl implements FreeWindowCalculator {

    /**
     * Computes all free time windows for a given cleaner on a given date.
     *
     */
    @Override
    public List<TimeWindowDTO> computeFreeWindows(Cleaner cleaner,
                                                  LocalDate date,
                                                  List<Booking> bookings) {

        log.debug("Computing free windows for cleaner={} on date={}", cleaner.getName(), date);

        LocalDateTime dayStart = getWorkdayStart(cleaner, date);
        LocalDateTime dayEnd = getWorkdayEnd(cleaner, date);

        List<Booking> normalized = normalizeAndSortBookings(bookings, dayStart, dayEnd);

        return calculateFreeWindows(normalized, dayStart, dayEnd, cleaner);
    }

    /**
     *
     * Normalizes and sorts bookings for availability calculation.
     *Bookings are always chronologically ordered before computing free windows.
     */
    private List<Booking> normalizeAndSortBookings(List<Booking> bookings,
                                                   LocalDateTime dayStart,
                                                   LocalDateTime dayEnd) {
        return bookings.stream()
                .map(b -> clampBookingToWorkday(b, dayStart, dayEnd))
                .filter(Objects::nonNull) // removes bookings that don't touch work hours
                .sorted(Comparator.comparing(Booking::getStartDateTime))
                .toList();
    }

    /**
     *
     * Only bookings relevant to availability calculation are considered.
     * Any booking that lies partially outside working hours is trimmed
     * so it never exceeds the allowed workday boundaries
     *
     */
    private Booking clampBookingToWorkday(Booking b,
                                          LocalDateTime dayStart,
                                          LocalDateTime dayEnd) {

        LocalDateTime start = b.getStartDateTime();
        LocalDateTime end = b.getEndDateTime().plusMinutes(ApplicationConstants.BREAK_MINUTES);

        // Booking fully outside work hours → ignore
        if (end.isBefore(dayStart) || start.isAfter(dayEnd)) {
            return null;
        }

        // Create a shallow copy with clamped values
        Booking clone = new Booking();
        clone.setStartDateTime(start.isBefore(dayStart) ? dayStart : start);
        clone.setEndDateTime(end.isAfter(dayEnd) ? dayEnd : end);

        return clone;
    }

    /**
     * Based on sorted, normalized bookings, this method computes all free windows
     */
    private List<TimeWindowDTO> calculateFreeWindows(
            List<Booking> bookings,
            LocalDateTime dayStart,
            LocalDateTime dayEnd,
            Cleaner cleaner) {

        List<TimeWindowDTO> windows = new ArrayList<>();
        LocalDateTime cursor = dayStart;

        for (Booking b : bookings) {
            if (cursor.isBefore(b.getStartDateTime())) {
                windows.add(new TimeWindowDTO(cursor.toLocalTime(), b.getStartDateTime().toLocalTime()));
            }
            cursor = b.getEndDateTime();
        }

        // Window after last booking
        if (cursor.isBefore(dayEnd)) {
            windows.add(new TimeWindowDTO(cursor.toLocalTime(), dayEnd.toLocalTime()));
        }

        log.debug("Free windows for {}: {}", cleaner.getName(), windows);
        return windows;
    }


    private LocalDateTime getWorkdayStart(Cleaner cleaner, LocalDate date) {
        LocalTime start = Optional.ofNullable(cleaner.getWorkStart())
                .orElse(ApplicationConstants.WORK_START);
        return LocalDateTime.of(date, start);
    }

    private LocalDateTime getWorkdayEnd(Cleaner cleaner, LocalDate date) {
        LocalTime end = Optional.ofNullable(cleaner.getWorkEnd())
                .orElse(ApplicationConstants.WORK_END);
        return LocalDateTime.of(date, end);
    }


}
