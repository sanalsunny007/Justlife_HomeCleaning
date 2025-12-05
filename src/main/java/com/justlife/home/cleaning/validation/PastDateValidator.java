package com.justlife.home.cleaning.validation;

import com.justlife.home.cleaning.enums.ApplicationErrorCode;
import com.justlife.home.cleaning.exception.BookingValidationException;
import com.justlife.home.cleaning.utils.ValidationUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PastDateValidator extends AbstractBookingValidator {

    @Override
    protected void check(LocalDateTime start, LocalDateTime end, int cleanerCount) {
        if (ValidationUtils.isPastDate(start.toLocalDate())) {
            throw new BookingValidationException(
                    ApplicationErrorCode.PAST_DATE_NOT_ALLOWED
            );
        }
    }
}
