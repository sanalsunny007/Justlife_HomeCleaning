package com.justlife.home.cleaning.validation;

import com.justlife.home.cleaning.enums.BookingErrorCode;
import com.justlife.home.cleaning.exception.BookingValidationException;

import java.time.Duration;
import java.time.LocalDateTime;

public class DurationValidator extends AbstractBookingValidator {

    @Override
    protected void check(LocalDateTime start, LocalDateTime end, int cleanerCount) {
        long hours = Duration.between(start, end).toHours();

        if (hours != 2 && hours != 4) {
            throw new BookingValidationException(BookingErrorCode.INVALID_DURATION);
        }

        if (!start.isBefore(end)) {
            throw new BookingValidationException(BookingErrorCode.INVALID_TIME_RANGE);
        }
    }
}
