package com.justlife.home.cleaning.validation;

import java.time.LocalDateTime;

public interface BookingValidator {
    void validate(LocalDateTime start, LocalDateTime end, int cleanerCount);
    BookingValidator linkWith(BookingValidator next);
}
