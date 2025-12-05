package com.justlife.home.cleaning.service.impl;

import com.justlife.home.cleaning.availability.rules.CleanerAvailabilityRule;
import com.justlife.home.cleaning.availability.strategy.CleanerAssignmentStrategy;
import com.justlife.home.cleaning.dto.BookingResponseDTO;
import com.justlife.home.cleaning.dto.CleanerDTO;
import com.justlife.home.cleaning.dto.CreateBookingRequestDTO;
import com.justlife.home.cleaning.dto.UpdateBookingRequestDTO;
import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;
import com.justlife.home.cleaning.enums.BookingStatus;
import com.justlife.home.cleaning.exception.NoAvailableCleanersException;
import com.justlife.home.cleaning.exception.ResourceNotFoundException;
import com.justlife.home.cleaning.repository.BookingRepository;
import com.justlife.home.cleaning.service.AvailabilityService;
import com.justlife.home.cleaning.service.BookingService;
import com.justlife.home.cleaning.utils.BookingMapper;
import com.justlife.home.cleaning.utils.CleanerMapper;
import com.justlife.home.cleaning.validation.BookingValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.justlife.home.cleaning.enums.ApplicationErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final AvailabilityService availabilityService;
    private final CleanerAssignmentStrategy assignmentStrategy;
    private final BookingValidator bookingValidator;
    private final CleanerAvailabilityRule cleanerAvailabilityRule;

    /**
     * Create a new booking
     */
    @Transactional
    @Override
    public BookingResponseDTO createBooking(CreateBookingRequestDTO req) {

        log.info("Creating booking for customer={}, start={}",
                req.getCustomerName(), req.getStartDateTime());

        LocalDateTime start = req.getStartDateTime();
        LocalDateTime end = start.plusHours(req.getDurationHours());

        bookingValidator.validate(start, end, req.getCleanerCount());

        List<CleanerDTO> availableCleaners = availabilityService.getAvailableCleaners(
                start.toLocalDate(),
                start.toLocalTime(),
                req.getDurationHours()
        );

        if (availableCleaners.isEmpty()) {
            throw new NoAvailableCleanersException(NO_CLEANERS_AVAILABLE);
        }

        List<CleanerDTO> assigned = assignmentStrategy.assignCleaners(
                availableCleaners,
                req.getCleanerCount()
        );

        Booking booking = Booking.builder()
                .startDateTime(start)
                .endDateTime(end)
                .durationHours(req.getDurationHours())
                .requiredCleanerCount(req.getCleanerCount())
                .customerName(req.getCustomerName())
                .status(BookingStatus.CONFIRMED)
                .cleaners(CleanerMapper.toEntityList(assigned))
                .build();

        Booking saved = bookingRepository.save(booking);

        log.info("Booking {} created successfully", saved.getId());

        return BookingMapper.toResponse(saved);
    }

    /**
     * Update an existing booking
     */
    @Transactional
    @Override
    public BookingResponseDTO updateBooking(Long id, UpdateBookingRequestDTO req) {

        log.info("Updating booking id={} newStart={}", id, req.getNewStartDateTime());

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(BOOKING_NOT_FOUND));

        LocalDateTime newStart = req.getNewStartDateTime();
        LocalDateTime newEnd = newStart.plusHours(booking.getDurationHours());

        bookingValidator.validate(newStart, newEnd, booking.getRequiredCleanerCount());

        List<Cleaner> updatedCleaners = booking.getCleaners().stream()
                .filter(cleaner -> cleanerAvailabilityRule.isCleanerAvailableForUpdate(
                        cleaner, booking.getId(), newStart.toLocalDate(), newStart, newEnd
                ))
                .collect(Collectors.toCollection(ArrayList::new));

        if (updatedCleaners.size() < booking.getRequiredCleanerCount()) {
            throw new NoAvailableCleanersException(INSUFFICIENT_CLEANERS_FOR_UPDATE);
        }

        booking.setStartDateTime(newStart);
        booking.setEndDateTime(newEnd);
        booking.setCleaners(updatedCleaners);

        Booking saved = bookingRepository.save(booking);
        log.info("Booking {} updated successfully", id);

        return BookingMapper.toResponse(saved);
    }

    @Override
    public BookingResponseDTO getBooking(Long id) {
        return bookingRepository.findById(id)
                .map(BookingMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(BOOKING_NOT_FOUND));
    }
}