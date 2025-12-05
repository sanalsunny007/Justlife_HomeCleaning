package com.justlife.home.cleaning.service;

import com.justlife.home.cleaning.availability.rules.CleanerAvailabilityRule;
import com.justlife.home.cleaning.availability.strategy.CleanerAssignmentStrategy;
import com.justlife.home.cleaning.dto.BookingResponseDTO;
import com.justlife.home.cleaning.dto.CleanerDTO;
import com.justlife.home.cleaning.dto.CreateBookingRequestDTO;
import com.justlife.home.cleaning.dto.UpdateBookingRequestDTO;
import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;
import com.justlife.home.cleaning.entity.Vehicle;
import com.justlife.home.cleaning.enums.BookingStatus;
import com.justlife.home.cleaning.enums.ApplicationErrorCode;
import com.justlife.home.cleaning.exception.NoAvailableCleanersException;
import com.justlife.home.cleaning.exception.ResourceNotFoundException;
import com.justlife.home.cleaning.repository.BookingRepository;
import com.justlife.home.cleaning.service.impl.BookingServiceImpl;
import com.justlife.home.cleaning.validation.BookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AvailabilityService availabilityService;

    @Mock
    private CleanerAssignmentStrategy assignmentStrategy;

    @Mock
    private BookingValidator bookingValidator;

    @Mock
    private CleanerAvailabilityRule cleanerAvailabilityRule;

    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                availabilityService,
                assignmentStrategy,
                bookingValidator,
                cleanerAvailabilityRule
        );
    }

    private CreateBookingRequestDTO createRequest() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setCustomerName("John Doe");
        dto.setCleanerCount(2);
        dto.setDurationHours(2);
        dto.setStartDateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        return dto;
    }

    private CleanerDTO cleanerDto(long id, long vehicleId) {
        return CleanerDTO.builder()
                .cleanerId(id)
                .cleanerName("Cleaner-" + id)
                .vehicleId(vehicleId)
                .vehicleName("Vehicle-" + vehicleId)
                .build();
    }

    private Cleaner cleaner(long id, Vehicle v) {
        Cleaner c = new Cleaner();
        c.setId(id);
        c.setName("Cleaner-" + id);
        c.setVehicle(v);
        return c;
    }

    @Test
    void createBooking_shouldPersistBooking_whenCleanersAvailable() {
        CreateBookingRequestDTO req = createRequest();

        CleanerDTO c1 = cleanerDto(1L, 10L);
        CleanerDTO c2 = cleanerDto(2L, 10L);

        when(availabilityService.getAvailableCleaners(
                req.getStartDateTime().toLocalDate(),
                req.getStartDateTime().toLocalTime(),
                req.getDurationHours()
        )).thenReturn(List.of(c1, c2));

        when(assignmentStrategy.assignCleaners(anyList(), eq(req.getCleanerCount())))
                .thenReturn(List.of(c1, c2));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> {
                    Booking b = invocation.getArgument(0);
                    b.setId(42L);
                    return b;
                });

        BookingResponseDTO response = bookingService.createBooking(req);

        assertNotNull(response.getId());
        assertEquals(42L, response.getId());
        assertEquals(req.getCustomerName(), response.getCustomerName());
        assertEquals(req.getCleanerCount(), response.getRequiredCleanerCount());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking savedBooking = captor.getValue();
        assertEquals(2, savedBooking.getCleaners().size());
    }

    @Test
    void createBooking_shouldThrow_whenNoCleanersAvailable() {
        CreateBookingRequestDTO req = createRequest();

        when(availabilityService.getAvailableCleaners(
                any(), any(), anyInt()
        )).thenReturn(List.of());

        NoAvailableCleanersException ex = assertThrows(
                NoAvailableCleanersException.class,
                () -> bookingService.createBooking(req)
        );

        assertEquals(ApplicationErrorCode.NO_CLEANERS_AVAILABLE.getCode(), ex.getErrorCode());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBooking_shouldUpdateTimes_whenEnoughCleanersRemain() {
        Long bookingId = 100L;
        UpdateBookingRequestDTO req = new UpdateBookingRequestDTO();
        LocalDateTime newStart = LocalDateTime.now().plusDays(2).withHour(12).withMinute(0);
        req.setNewStartDateTime(newStart);

        Vehicle v = new Vehicle();
        v.setId(10L);
        v.setName("Vehicle-1");

        Cleaner c1 = cleaner(1L, v);
        Cleaner c2 = cleaner(2L, v);

        Booking booking = Booking.builder()
                .id(bookingId)
                .startDateTime(LocalDateTime.now().plusDays(1).withHour(10))
                .endDateTime(LocalDateTime.now().plusDays(1).withHour(12))
                .durationHours(2)
                .requiredCleanerCount(2)
                .customerName("John Doe")
                .status(BookingStatus.CONFIRMED)
                .cleaners(List.of(c1, c2))
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(cleanerAvailabilityRule.isCleanerAvailableForUpdate(
                any(), eq(bookingId), any(), any(), any()
        )).thenReturn(true);

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO response = bookingService.updateBooking(bookingId, req);

        assertEquals(newStart, response.getStartDateTime());
        assertEquals(newStart.plusHours(2), response.getEndDateTime());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void updateBooking_shouldThrow_whenInsufficientCleanersRemain() {
        Long bookingId = 100L;
        UpdateBookingRequestDTO req = new UpdateBookingRequestDTO();
        LocalDateTime newStart = LocalDateTime.now().plusDays(2).withHour(12).withMinute(0);
        req.setNewStartDateTime(newStart);

        Vehicle v = new Vehicle();
        v.setId(10L);
        v.setName("Vehicle-1");

        Cleaner c1 = cleaner(1L, v);
        Cleaner c2 = cleaner(2L, v);

        Booking booking = Booking.builder()
                .id(bookingId)
                .startDateTime(LocalDateTime.now().plusDays(1).withHour(10))
                .endDateTime(LocalDateTime.now().plusDays(1).withHour(12))
                .durationHours(2)
                .requiredCleanerCount(2)
                .customerName("John Doe")
                .status(BookingStatus.CONFIRMED)
                .cleaners(List.of(c1, c2))
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Both cleaners become unavailable
        when(cleanerAvailabilityRule.isCleanerAvailableForUpdate(
                any(), eq(bookingId), any(), any(), any()
        )).thenReturn(false);

        NoAvailableCleanersException ex = assertThrows(
                NoAvailableCleanersException.class,
                () -> bookingService.updateBooking(bookingId, req)
        );

        assertEquals(ApplicationErrorCode.INSUFFICIENT_CLEANERS_FOR_UPDATE.getCode(), ex.getErrorCode());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBooking_shouldThrow_whenNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.getBooking(1L)
        );

        //ApplicationErrorCode.BOOKING_NOT_FOUND
        assertEquals(ApplicationErrorCode.BOOKING_NOT_FOUND.getCode(), ex.getErrorCode());
    }
}
