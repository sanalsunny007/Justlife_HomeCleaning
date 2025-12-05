package com.justlife.home.cleaning.controller;

import com.justlife.home.cleaning.dto.BookingResponseDTO;
import com.justlife.home.cleaning.dto.CreateBookingRequestDTO;
import com.justlife.home.cleaning.dto.ErrorResponseDTO;
import com.justlife.home.cleaning.dto.UpdateBookingRequestDTO;
import com.justlife.home.cleaning.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@Tag(name = "Booking API", description = "Create, update, and retrieve bookings")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // -------------------------------------------------------------------------
    // CREATE BOOKING
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Create a new booking",
            description = """
                    Creates a new booking and assigns 1–3 cleaners from the same vehicle.
                    Validates working hours, non-working days, duration rules, and vehicle capacity.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Booking created successfully",
            content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error — invalid duration, cleaners count, working hours, etc.",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Business rule violation — no available cleaners or no vehicle has enough cleaners",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @PostMapping
    public BookingResponseDTO createBooking(
            @Valid @RequestBody CreateBookingRequestDTO request
    ) {
        return bookingService.createBooking(request);
    }

    // -------------------------------------------------------------------------
    // UPDATE BOOKING
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Update an existing booking",
            description = """
                    Updates the booking start time. The system attempts to preserve existing cleaners.
                    If not possible, it reallocates from the same vehicle. Otherwise, update fails.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Booking updated successfully",
            content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Not enough cleaners available to update booking",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @PutMapping("/{id}")
    public BookingResponseDTO updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingRequestDTO request
    ) {
        return bookingService.updateBooking(id, request);
    }

    // -------------------------------------------------------------------------
    // GET BOOKING
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Get booking details",
            description = """
                    Retrieves booking details, including assigned cleaner IDs, vehicle,
                    duration, status, and scheduled time.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Booking details retrieved successfully",
            content = @Content(schema = @Schema(implementation = BookingResponseDTO.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
    )
    @GetMapping("/{id}")
    public BookingResponseDTO getBooking(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }
}
