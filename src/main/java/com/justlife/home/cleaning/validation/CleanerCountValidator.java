package com.justlife.home.cleaning.validation;

import com.justlife.home.cleaning.enums.BookingErrorCode;
import com.justlife.home.cleaning.exception.BookingValidationException;

import java.time.LocalDateTime;

public class CleanerCountValidator extends AbstractBookingValidator {

    @Override
    protected void check(LocalDateTime start, LocalDateTime end, int cleanerCount) {
        if (cleanerCount < 1 || cleanerCount > 3) {
            throw new BookingValidationException(BookingErrorCode.INVALID_CLEANER_COUNT);
        }
    }
}
