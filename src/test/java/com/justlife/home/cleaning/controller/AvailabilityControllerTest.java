package com.justlife.home.cleaning.controller;

import com.justlife.home.cleaning.dto.CleanerAvailabilityDTO;
import com.justlife.home.cleaning.dto.CleanerDTO;
import com.justlife.home.cleaning.dto.TimeWindowDTO;
import com.justlife.home.cleaning.enums.ApplicationErrorCode;
import com.justlife.home.cleaning.exception.BookingValidationException;
import com.justlife.home.cleaning.service.AvailabilityService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AvailabilityControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AvailabilityService availabilityService;

    // -------------------------------------------------------------
    @Test
    void shouldRejectPastDates() throws Exception {

        doThrow(new BookingValidationException(ApplicationErrorCode.PAST_DATE_NOT_ALLOWED))
                .when(availabilityService).getAvailabilityForDate(any());

        mvc.perform(get("/api/v1/availability")
                        .param("date", "2020-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ApplicationErrorCode.PAST_DATE_NOT_ALLOWED.getCode()));
    }

    // -------------------------------------------------------------
    @Test
    void shouldReturnCleanerAvailability() throws Exception {

        CleanerAvailabilityDTO dto = CleanerAvailabilityDTO.builder()
                .cleanerId(1L)
                .cleanerName("John")
                .vehicleId(10L)
                .vehicleName("Car-A")
                .availableWindows(
                        List.of(new TimeWindowDTO(
                                LocalTime.of(10, 0),
                                LocalTime.of(12, 0)
                        ))
                )
                .build();

        when(availabilityService.getAvailabilityForDate(LocalDate.of(2025, 12, 10)))
                .thenReturn(List.of(dto));

        mvc.perform(get("/api/v1/availability")
                        .param("date", "2025-12-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cleanerId").value(1L))
                .andExpect(jsonPath("$[0].vehicleName").value("Car-A"));
    }

    // -------------------------------------------------------------
    @Test
    void shouldReturnCleanersForSlot() throws Exception {

        CleanerDTO dto = CleanerDTO.builder()
                .cleanerId(2L)
                .cleanerName("Aisha")
                .vehicleId(5L)
                .vehicleName("Car-5")
                .build();

        when(availabilityService.getAvailableCleaners(any(), any(), eq(2)))
                .thenReturn(List.of(dto));

        mvc.perform(get("/api/v1/availability/slot")
                        .param("date", "2025-12-10")
                        .param("start", "10:00")
                        .param("durationHours", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cleanerName").value("Aisha"));
    }
}
