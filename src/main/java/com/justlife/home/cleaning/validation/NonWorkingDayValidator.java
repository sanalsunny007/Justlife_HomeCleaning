package com.justlife.home.cleaning.validation;

import com.justlife.home.cleaning.enums.BookingErrorCode;
import com.justlife.home.cleaning.exception.BookingValidationException;
import com.justlife.home.cleaning.utils.ValidationUtils;

import java.time.LocalDateTime;

public class NonWorkingDayValidator extends AbstractBookingValidator {

    @Override
    protected void check(LocalDateTime start, LocalDateTime end, int cleanerCount) {
        if (ValidationUtils.isNonWorkingDay(start.toLocalDate())) {
            throw new BookingValidationException(BookingErrorCode.NON_WORKING_DAY);
        }
    }
}
