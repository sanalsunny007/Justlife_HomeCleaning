package com.justlife.home.cleaning.validation;

import com.justlife.home.cleaning.enums.BookingErrorCode;
import com.justlife.home.cleaning.exception.BookingValidationException;
import com.justlife.home.cleaning.utils.ValidationUtils;

import java.time.LocalDateTime;

public class WorkingHoursValidator extends AbstractBookingValidator {

    @Override
    protected void check(LocalDateTime start, LocalDateTime end, int cleanerCount) {
        if (ValidationUtils.isOutsideWorkingHours(start, end)) {
            throw new BookingValidationException(BookingErrorCode.OUTSIDE_WORKING_HOURS);
        }
    }
}
