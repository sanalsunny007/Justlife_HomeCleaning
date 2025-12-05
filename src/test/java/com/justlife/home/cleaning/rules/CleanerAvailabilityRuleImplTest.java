package com.justlife.home.cleaning.rules;

import com.justlife.home.cleaning.availability.rules.CleanerAvailabilityRule;
import com.justlife.home.cleaning.availability.rules.CleanerAvailabilityRuleImpl;
import com.justlife.home.cleaning.constants.ApplicationConstants;
import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;
import com.justlife.home.cleaning.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CleanerAvailabilityRuleImplTest {

    @Mock
    private BookingRepository bookingRepository;

    private CleanerAvailabilityRule rule;
    private Cleaner cleaner;

    @BeforeEach
    void setup() {
        rule = new CleanerAvailabilityRuleImpl(bookingRepository);

        cleaner = new Cleaner();
        cleaner.setId(1L);
        cleaner.setName("Cleaner-A");
    }

    private Booking booking(Long id, LocalDateTime start, LocalDateTime end) {
        Booking b = new Booking();
        b.setId(id);
        b.setStartDateTime(start);
        b.setEndDateTime(end);
        return b;
    }

    @Test
    void isCleanerAvailable_shouldReturnFalse_whenOverlappingWithBreak() {
        LocalDate date = LocalDate.now().plusDays(1);

        // Existing booking: 10:00–12:00
        Booking existing = booking(100L, date.atTime(10, 0), date.atTime(12, 0));

        // New requested time: 11:50–13:00 (should overlap due to BREAK_MINUTES)
        LocalDateTime start = date.atTime(11, 50);
        LocalDateTime end = date.atTime(13, 0);

        boolean result = rule.isCleanerAvailable(cleaner, date, start, end, List.of(existing));

        assertFalse(result, "Should reject because overlap occurs within break buffer");
    }

    @Test
    void isCleanerAvailable_shouldReturnTrue_whenNoOverlapEvenWithBreak() {
        LocalDate date = LocalDate.now().plusDays(1);

        Booking existing = booking(100L, date.atTime(10, 0), date.atTime(12, 0));

        // New request far enough after break buffer
        LocalDateTime start = date.atTime(12, 30)
                .plusMinutes(ApplicationConstants.BREAK_MINUTES);
        LocalDateTime end = start.plusHours(2);

        boolean result = rule.isCleanerAvailable(cleaner, date, start, end, List.of(existing));

        assertTrue(result, "Should allow booking when break buffer gap is respected");
    }

    @Test
    void isCleanerAvailable_shouldReturnFalse_whenMultipleBookingsConflict() {
        LocalDate date = LocalDate.now().plusDays(1);

        Booking b1 = booking(1L, date.atTime(8, 0), date.atTime(10, 0));
        Booking b2 = booking(2L, date.atTime(12, 0), date.atTime(14, 0));

        // New request overlaps b2 by break buffer
        LocalDateTime start = date.atTime(13, 50);
        LocalDateTime end = date.atTime(15, 0);

        boolean result = rule.isCleanerAvailable(cleaner, date, start, end, List.of(b1, b2));

        assertFalse(result, "Should detect conflict with second booking");
    }

    @Test
    void isCleanerAvailableForUpdate_shouldIgnoreSameBookingId() {
        LocalDate date = LocalDate.now().plusDays(1);

        Booking existing = booking(100L, date.atTime(10, 0), date.atTime(12, 0));

        when(bookingRepository.findByCleanerAndDate(cleaner, date))
                .thenReturn(List.of(existing));

        // Updating same booking → must ignore conflict
        LocalDateTime newStart = date.atTime(11, 0);
        LocalDateTime newEnd = date.atTime(13, 0);

        boolean result = rule.isCleanerAvailableForUpdate(
                cleaner,
                100L,
                date,
                newStart,
                newEnd
        );

        assertTrue(result, "Same booking ID must be ignored during update");
    }

    @Test
    void isCleanerAvailableForUpdate_shouldReturnFalse_whenConflictingOtherBooking() {
        LocalDate date = LocalDate.now().plusDays(1);

        Booking currentBooking = booking(100L, date.atTime(9, 0), date.atTime(11, 0));
        Booking anotherBooking = booking(200L, date.atTime(12, 0), date.atTime(14, 0));

        when(bookingRepository.findByCleanerAndDate(cleaner, date))
                .thenReturn(List.of(currentBooking, anotherBooking));

        // New time overlaps booking 200 → should return false
        LocalDateTime newStart = date.atTime(13, 50);
        LocalDateTime newEnd = date.atTime(15, 0);

        boolean result = rule.isCleanerAvailableForUpdate(
                cleaner,
                100L,      // Updating booking 100
                date,
                newStart,
                newEnd
        );

        assertFalse(result, "Should detect conflict with another booking during update");
    }

    @Test
    void isCleanerAvailableForUpdate_shouldReturnTrue_whenNoConflicts() {
        LocalDate date = LocalDate.now().plusDays(1);

        Booking b1 = booking(11L, date.atTime(8, 0), date.atTime(10, 0));
        Booking b2 = booking(12L, date.atTime(14, 0), date.atTime(16, 0));

        when(bookingRepository.findByCleanerAndDate(cleaner, date))
                .thenReturn(List.of(b1, b2));

        // New request sits between two bookings peacefully
        LocalDateTime newStart = date.atTime(10, 30);
        LocalDateTime newEnd = date.atTime(12, 30);

        boolean result = rule.isCleanerAvailableForUpdate(
                cleaner,
                999L,  // Updating unrelated booking
                date,
                newStart,
                newEnd
        );

        assertTrue(result, "Should allow update because no conflict occurs");
    }
}
