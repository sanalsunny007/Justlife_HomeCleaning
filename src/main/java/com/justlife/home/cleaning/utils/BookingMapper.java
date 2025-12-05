package com.justlife.home.cleaning.utils;

import com.justlife.home.cleaning.dto.BookingResponseDTO;
import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static BookingResponseDTO toResponse(Booking booking) {

        List<Long> cleanerIds = booking.getCleaners()
                .stream()
                .map(Cleaner::getId)
                .toList();

        Long vehicleId = booking.getCleaners().isEmpty()
                ? null
                : booking.getCleaners().getFirst().getVehicle().getId();

        return BookingResponseDTO.builder()
                .id(booking.getId())
                .startDateTime(booking.getStartDateTime())
                .endDateTime(booking.getEndDateTime())
                .durationHours(booking.getDurationHours())
                .requiredCleanerCount(booking.getRequiredCleanerCount())
                .customerName(booking.getCustomerName())
                .status(booking.getStatus())
                .cleanerIds(cleanerIds)
                .vehicleId(vehicleId)
                .build();
    }

}
