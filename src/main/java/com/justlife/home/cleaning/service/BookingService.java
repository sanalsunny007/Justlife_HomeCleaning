package com.justlife.home.cleaning.service;

import com.justlife.home.cleaning.dto.BookingResponseDTO;
import com.justlife.home.cleaning.dto.CreateBookingRequestDTO;
import com.justlife.home.cleaning.dto.UpdateBookingRequestDTO;
import jakarta.transaction.Transactional;

public interface BookingService {
    @Transactional
    BookingResponseDTO createBooking(CreateBookingRequestDTO req);

    @Transactional
    BookingResponseDTO updateBooking(Long id, UpdateBookingRequestDTO req);

    BookingResponseDTO getBooking(Long id);
}
