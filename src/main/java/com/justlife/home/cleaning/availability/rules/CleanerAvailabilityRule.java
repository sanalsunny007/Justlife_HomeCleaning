package com.justlife.home.cleaning.availability.rules;

import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CleanerAvailabilityRule {
    boolean isCleanerAvailable(
            Cleaner cleaner,
            LocalDate date,
            LocalDateTime start,
            LocalDateTime end,
            List<Booking> bookings
    );

    boolean isCleanerAvailableForUpdate(Cleaner cleaner,
                                        Long bookingId,
                                        LocalDate date,
                                        LocalDateTime start,
                                        LocalDateTime end);
}
