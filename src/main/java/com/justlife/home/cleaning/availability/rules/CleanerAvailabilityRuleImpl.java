package com.justlife.home.cleaning.availability.rules;

import com.justlife.home.cleaning.constants.ApplicationConstants;
import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;
import com.justlife.home.cleaning.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of CleanerAvailabilityRule responsible for checking
 * whether a cleaner is free for a requested booking time slot.
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CleanerAvailabilityRuleImpl implements CleanerAvailabilityRule {

    private final BookingRepository bookingRepository;

    /**
     * Validates whether a cleaner is available for a new booking.
     * Applies break buffer and checks for time overlap with existing bookings.
     */
    @Override
    public boolean isCleanerAvailable(
            Cleaner cleaner,
            LocalDate date,
            LocalDateTime start,
            LocalDateTime end,
            List<Booking> bookings) {

        for (Booking booking : bookings) {
            if (isOverlappingWithBreak(start, end, booking)) {
                logConflict(cleaner, booking, "new booking");
                return false;
            }
        }

        return true;
    }

    /**
     * Validates availability for booking updates.
     * Ignores the booking being updated.
     */
    @Override
    public boolean isCleanerAvailableForUpdate(
            Cleaner cleaner,
            Long bookingId,
            LocalDate date,
            LocalDateTime start,
            LocalDateTime end) {

        List<Booking> bookings = bookingRepository.findByCleanerAndDate(cleaner, date);

        for (Booking booking : bookings) {

            if (Objects.equals(booking.getId(), bookingId)) {
                continue; // Skip the booking being updated
            }

            if (isOverlappingWithBreak(start, end, booking)) {
                logConflict(cleaner, booking, "update request");
                return false;
            }
        }

        return true;
    }

    // Private Helpers

    /**
     * Applies break buffer and checks if (start, end) overlaps with an existing booking.
     */
    private boolean isOverlappingWithBreak(LocalDateTime start,
                                           LocalDateTime end,
                                           Booking booking) {

        LocalDateTime adjustedStart = booking.getStartDateTime()
                .minusMinutes(ApplicationConstants.BREAK_MINUTES);

        LocalDateTime adjustedEnd = booking.getEndDateTime()
                .plusMinutes(ApplicationConstants.BREAK_MINUTES);

        // Overlap rule: start < adjustedEnd && end > adjustedStart
        return start.isBefore(adjustedEnd) && end.isAfter(adjustedStart);
    }

    /**
     * Logs booking conflict in a consistent, reusable way.
     */
    private void logConflict(Cleaner cleaner, Booking booking, String context) {

        LocalDateTime adjStart = booking.getStartDateTime()
                .minusMinutes(ApplicationConstants.BREAK_MINUTES);

        LocalDateTime adjEnd = booking.getEndDateTime()
                .plusMinutes(ApplicationConstants.BREAK_MINUTES);

        log.debug(
                "Cleaner '{}' unavailable for {} due to conflict with booking {} [{} - {}]",
                cleaner.getName(),
                context,
                booking.getId(),
                adjStart,
                adjEnd
        );
    }
}

