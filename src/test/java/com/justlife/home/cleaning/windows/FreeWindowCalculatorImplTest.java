package com.justlife.home.cleaning.windows;

import com.justlife.home.cleaning.availability.windows.FreeWindowCalculator;
import com.justlife.home.cleaning.availability.windows.FreeWindowCalculatorImpl;
import com.justlife.home.cleaning.constants.ApplicationConstants;
import com.justlife.home.cleaning.dto.TimeWindowDTO;
import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FreeWindowCalculatorImplTest {

    private FreeWindowCalculator calculator;
    private Cleaner cleaner;
    private final LocalDate date = LocalDate.of(2025, 12, 5);

    @BeforeEach
    void setup() {
        calculator = new FreeWindowCalculatorImpl();
        cleaner = new Cleaner();
        cleaner.setName("Test Cleaner");
        cleaner.setWorkStart(LocalTime.of(8, 0));
        cleaner.setWorkEnd(LocalTime.of(22, 0));
    }

    private Booking booking(String start, String end) {
        Booking b = new Booking();
        b.setStartDateTime(LocalDateTime.of(date, LocalTime.parse(start)));
        b.setEndDateTime(LocalDateTime.of(date, LocalTime.parse(end)));
        return b;
    }

    // -------------------------------------------------------------------------
    // TEST CASE 1: No Bookings → Whole day is free
    // -------------------------------------------------------------------------
    @Test
    void shouldReturnFullDayWindow_whenNoBookings() {
        List<TimeWindowDTO> windows = calculator.computeFreeWindows(cleaner, date, List.of());

        assertEquals(1, windows.size());
        assertEquals(LocalTime.of(8, 0), windows.getFirst().getStart());
        assertEquals(LocalTime.of(22, 0), windows.getFirst().getEnd());
    }

    // -------------------------------------------------------------------------
    // TEST CASE 2: Single booking in middle of day
    // -------------------------------------------------------------------------
    @Test
    void shouldReturnTwoWindows_whenSingleBookingExists() {
        Booking b = booking("10:00", "12:00");

        List<TimeWindowDTO> windows = calculator.computeFreeWindows(cleaner, date, List.of(b));

        // Break buffer applies → endTime = 12:00 + BREAK_MINUTES
        LocalTime adjustedEnd = LocalTime.of(12, 0)
                .plusMinutes(ApplicationConstants.BREAK_MINUTES);

        assertEquals(2, windows.size());

        assertEquals(LocalTime.of(8, 0), windows.get(0).getStart());
        assertEquals(LocalTime.of(10, 0), windows.get(0).getEnd());

        assertEquals(adjustedEnd, windows.get(1).getStart());
        assertEquals(LocalTime.of(22, 0), windows.get(1).getEnd());
    }

    // -------------------------------------------------------------------------
    // TEST CASE 3: Multiple bookings
    // -------------------------------------------------------------------------
    @Test
    void shouldReturnCorrectWindows_forMultipleBookings() {
        Booking b1 = booking("09:00", "10:00");
        Booking b2 = booking("13:00", "14:00");

        List<TimeWindowDTO> windows =
                calculator.computeFreeWindows(cleaner, date, List.of(b1, b2));

        assertEquals(3, windows.size());

        assertEquals(LocalTime.of(8, 0), windows.get(0).getStart());
        assertEquals(LocalTime.of(9, 0), windows.get(0).getEnd());

        assertEquals(LocalTime.of(10, 0).plusMinutes(ApplicationConstants.BREAK_MINUTES),
                windows.get(1).getStart());
        assertEquals(LocalTime.of(13, 0), windows.get(1).getEnd());

        assertEquals(LocalTime.of(14, 0).plusMinutes(ApplicationConstants.BREAK_MINUTES),
                windows.get(2).getStart());
        assertEquals(LocalTime.of(22, 0), windows.get(2).getEnd());
    }

    // -------------------------------------------------------------------------
    // TEST CASE 4: Booking completely outside work hours → ignored
    // -------------------------------------------------------------------------
    @Test
    void shouldIgnoreBookingOutsideWorkingHours() {
        // before 08:00 and after 22:00
        Booking early = booking("06:00", "07:00");
        Booking late = booking("23:00", "23:30");

        List<TimeWindowDTO> windows = calculator.computeFreeWindows(
                cleaner, date, List.of(early, late));

        assertEquals(1, windows.size());
        assertEquals(LocalTime.of(8, 0), windows.getFirst().getStart());
        assertEquals(LocalTime.of(22, 0), windows.getFirst().getEnd());
    }

    // -------------------------------------------------------------------------
    // TEST CASE 5: Booking that starts before workday → clamped
    // -------------------------------------------------------------------------
    @Test
    void shouldClampBookingStart_whenStartsBeforeWorkday() {
        Booking b = booking("06:00", "09:00");

        List<TimeWindowDTO> windows = calculator.computeFreeWindows(cleaner, date, List.of(b));

        // Free window from 8:00 → 8:00 (none)
        // After booking ends + break
        LocalTime expectedStart = LocalTime.of(9, 0)
                .plusMinutes(ApplicationConstants.BREAK_MINUTES);

        assertEquals(1, windows.size());
        assertEquals(expectedStart, windows.getFirst().getStart());
        assertEquals(LocalTime.of(22, 0), windows.getFirst().getEnd());
    }

    // -------------------------------------------------------------------------
    // TEST CASE 6: Booking that ends after workday → clamped
    // -------------------------------------------------------------------------
    @Test
    void shouldClampBookingEnd_whenEndsAfterWorkday() {
        Booking b = booking("20:00", "23:30");

        List<TimeWindowDTO> windows = calculator.computeFreeWindows(cleaner, date, List.of(b));

        // Free window from 8 → 20
        assertEquals(1, windows.size());
        assertEquals(LocalTime.of(8, 0), windows.getFirst().getStart());
        assertEquals(LocalTime.of(20, 0), windows.getFirst().getEnd());
    }
}
