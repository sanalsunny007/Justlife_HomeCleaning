package com.justlife.home.cleaning.service;

import com.justlife.home.cleaning.availability.rules.CleanerAvailabilityRule;
import com.justlife.home.cleaning.availability.windows.FreeWindowCalculator;
import com.justlife.home.cleaning.dto.CleanerAvailabilityDTO;
import com.justlife.home.cleaning.dto.TimeWindowDTO;
import com.justlife.home.cleaning.entity.Cleaner;
import com.justlife.home.cleaning.entity.Vehicle;
import com.justlife.home.cleaning.enums.ApplicationErrorCode;
import com.justlife.home.cleaning.enums.BookingErrorCode;
import com.justlife.home.cleaning.exception.BookingValidationException;
import com.justlife.home.cleaning.repository.BookingRepository;
import com.justlife.home.cleaning.repository.CleanerRepository;
import com.justlife.home.cleaning.service.impl.AvailabilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private CleanerRepository cleanerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CleanerAvailabilityRule cleanerAvailabilityRule;

    @Mock
    private FreeWindowCalculator freeWindowCalculator;

    private AvailabilityServiceImpl availabilityService;

    @BeforeEach
    void setUp() {
        availabilityService = new AvailabilityServiceImpl(
                cleanerRepository,
                bookingRepository,
                cleanerAvailabilityRule,
                freeWindowCalculator
        );
    }

    private LocalDate futureNonFriday() {
        LocalDate date = LocalDate.now().plusDays(2);
        while (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
            date = date.plusDays(1);
        }
        return date;
    }

    private Cleaner createCleaner() {
        Vehicle v = new Vehicle();
        v.setId(10L);
        v.setName("Vehicle-1");

        Cleaner c = new Cleaner();
        c.setId(1L);
        c.setName("Cleaner-1");
        c.setVehicle(v);
        return c;
    }

    @Test
    void getAvailabilityForDate_shouldReturnAvailabilityDTO_whenFreeWindowsExist() {
        LocalDate date = futureNonFriday();

        Cleaner cleaner = createCleaner();

        when(cleanerRepository.findAll()).thenReturn(List.of(cleaner));
        when(bookingRepository.findByDate(date)).thenReturn(Collections.emptyList());

        TimeWindowDTO window = new TimeWindowDTO(LocalTime.of(9, 0), LocalTime.of(11, 0));
        when(freeWindowCalculator.computeFreeWindows(cleaner, date, Collections.emptyList()))
                .thenReturn(List.of(window));

        List<CleanerAvailabilityDTO> result = availabilityService.getAvailabilityForDate(date);

        assertEquals(1, result.size());
        CleanerAvailabilityDTO dto = result.getFirst();
        assertEquals(cleaner.getId(), dto.getCleanerId());
        assertEquals(cleaner.getVehicle().getId(), dto.getVehicleId());
        assertEquals(1, dto.getAvailableWindows().size());
    }

    @Test
    void getAvailabilityForDate_shouldThrow_forPastDate() {
        LocalDate past = LocalDate.now().minusDays(1);

        BookingValidationException ex = assertThrows(
                BookingValidationException.class,
                () -> availabilityService.getAvailabilityForDate(past)
        );

        assertEquals(ApplicationErrorCode.PAST_DATE_NOT_ALLOWED.getCode(), ex.getErrorCode());
    }

    @Test
    void getAvailableCleaners_shouldThrow_forInvalidDuration() {
        LocalDate date = futureNonFriday();
        LocalTime start = LocalTime.of(10, 0);

        // duration 3h is invalid → BookingValidationException(INVALID_DURATION)
        BookingValidationException ex = assertThrows(
                BookingValidationException.class,
                () -> availabilityService.getAvailableCleaners(date, start, 3)
        );

        assertEquals(BookingErrorCode.INVALID_DURATION.getCode(), ex.getErrorCode());
    }

    @Test
    void getAvailableCleaners_shouldReturnEmpty_onNonWorkingDay() {
        // Find next Friday
        LocalDate friday = LocalDate.now().plusDays(1);
        while (friday.getDayOfWeek() != DayOfWeek.FRIDAY) {
            friday = friday.plusDays(1);
        }

        List<?> result = availabilityService.getAvailableCleaners(friday, LocalTime.of(10, 0), 2);
        assertTrue(result.isEmpty());
        verifyNoInteractions(cleanerRepository);
    }

    @Test
    void getAvailableCleaners_shouldReturnCleaner_whenRuleAllows() {
        LocalDate date = futureNonFriday();
        LocalTime startTime = LocalTime.of(10, 0);
        int duration = 2;

        Cleaner cleaner = createCleaner();

        when(cleanerRepository.findAll()).thenReturn(List.of(cleaner));
        when(bookingRepository.findByCleanerAndDate(eq(cleaner), eq(date)))
                .thenReturn(Collections.emptyList());

        when(cleanerAvailabilityRule.isCleanerAvailable(
                eq(cleaner),
                eq(date),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(Collections.emptyList())
        )).thenReturn(true);

        var result = availabilityService.getAvailableCleaners(date, startTime, duration);

        assertEquals(1, result.size());
        assertEquals(cleaner.getId(), result.getFirst().getCleanerId());
    }
}
