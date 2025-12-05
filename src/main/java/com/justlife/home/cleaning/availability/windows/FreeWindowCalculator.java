package com.justlife.home.cleaning.availability.windows;

import com.justlife.home.cleaning.dto.TimeWindowDTO;
import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;

import java.time.LocalDate;
import java.util.List;

public interface FreeWindowCalculator {

    List<TimeWindowDTO> computeFreeWindows(
            Cleaner cleaner,
            LocalDate date,
            List<Booking> bookings
    );
}
