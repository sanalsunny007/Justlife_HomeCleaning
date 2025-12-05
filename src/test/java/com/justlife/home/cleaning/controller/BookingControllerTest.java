package com.justlife.home.cleaning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.justlife.home.cleaning.dto.BookingResponseDTO;
import com.justlife.home.cleaning.dto.CreateBookingRequestDTO;
import com.justlife.home.cleaning.dto.UpdateBookingRequestDTO;
import com.justlife.home.cleaning.enums.ApplicationErrorCode;
import com.justlife.home.cleaning.exception.NoAvailableCleanersException;
import com.justlife.home.cleaning.exception.ResourceNotFoundException;
import com.justlife.home.cleaning.service.BookingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper;

    @MockitoBean
    private BookingService bookingService;

    @BeforeEach
    void setup() {
        mapper = JsonMapper.builder().findAndAddModules().build();
    }


    // -------------------------------------------------------------------------
    // CREATE BOOKING
    // -------------------------------------------------------------------------
    @Test
    void shouldCreateBookingSuccessfully() throws Exception {

        CreateBookingRequestDTO request = new CreateBookingRequestDTO(
                LocalDateTime.of(2025, 12, 10, 10, 0),
                2,
                2,
                "Sanal Sunny"
        );

        BookingResponseDTO response = BookingResponseDTO.builder()
                .id(1L)
                .customerName("Sanal Sunny")
                .durationHours(2)
                .requiredCleanerCount(2)
                .build();

        when(bookingService.createBooking(any())).thenReturn(response);

        mvc.perform(post("/api/v1/bookings")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerName").value("Sanal Sunny"));
    }

    @Test
    void shouldReturn400_whenValidationFails() throws Exception {

        CreateBookingRequestDTO request = new CreateBookingRequestDTO(
                LocalDateTime.of(2025, 12, 10, 23, 0), // invalid time slot
                2,
                10, // invalid cleaner count → validation failure
                "Test User"
        );

        doThrow(new NoAvailableCleanersException(ApplicationErrorCode.VALIDATION_FAILED))
                .when(bookingService).createBooking(any());

        mvc.perform(post("/api/v1/bookings")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(
                        ApplicationErrorCode.VALIDATION_FAILED.getCode()
                ));
    }

    @Test
    void shouldReturn409_whenNoVehicleHasEnoughCleaners() throws Exception {

        CreateBookingRequestDTO request = new CreateBookingRequestDTO(

                LocalDateTime.of(2025, 12, 12, 14, 0),
                4,
                3,
                "User"
        );

        doThrow(new NoAvailableCleanersException(ApplicationErrorCode.NO_VEHICLE_WITH_REQUIRED_CLEANERS))
                .when(bookingService).createBooking(any());

        mvc.perform(post("/api/v1/bookings")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value(
                        ApplicationErrorCode.NO_VEHICLE_WITH_REQUIRED_CLEANERS.getCode()
                ));
    }

    // -------------------------------------------------------------------------
    // UPDATE BOOKING
    // -------------------------------------------------------------------------
    @Test
    void shouldUpdateBookingSuccessfully() throws Exception {

        UpdateBookingRequestDTO request = new UpdateBookingRequestDTO(
                LocalDateTime.of(2025, 12, 15, 12, 0)
        );

        BookingResponseDTO response = BookingResponseDTO.builder()
                .id(10L)
                .customerName("Test User")
                .durationHours(2)
                .requiredCleanerCount(2)
                .build();

        when(bookingService.updateBooking(any(), any())).thenReturn(response);

        mvc.perform(put("/api/v1/bookings/10")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    void shouldReturn404_whenUpdatingNonExistingBooking() throws Exception {

        UpdateBookingRequestDTO request = new UpdateBookingRequestDTO(
                LocalDateTime.of(2025, 12, 15, 12, 0)
        );

        doThrow(new ResourceNotFoundException(ApplicationErrorCode.BOOKING_NOT_FOUND))
                .when(bookingService).updateBooking(any(), any());

        mvc.perform(put("/api/v1/bookings/999")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(
                        ApplicationErrorCode.BOOKING_NOT_FOUND.getCode()
                ));
    }

    // -------------------------------------------------------------------------
    // GET BOOKING
    // -------------------------------------------------------------------------
    @Test
    void shouldReturnBookingById() throws Exception {

        BookingResponseDTO response = BookingResponseDTO.builder()
                .id(5L)
                .customerName("Customer X")
                .durationHours(4)
                .requiredCleanerCount(3)
                .build();

        when(bookingService.getBooking(5L)).thenReturn(response);

        mvc.perform(get("/api/v1/bookings/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.customerName").value("Customer X"));
    }

    @Test
    void shouldReturn404_whenBookingNotFound() throws Exception {

        doThrow(new ResourceNotFoundException(ApplicationErrorCode.BOOKING_NOT_FOUND))
                .when(bookingService).getBooking(99L);

        mvc.perform(get("/api/v1/bookings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(
                        ApplicationErrorCode.BOOKING_NOT_FOUND.getCode()
                ));
    }
}
