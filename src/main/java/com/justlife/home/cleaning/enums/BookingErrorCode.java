package com.justlife.home.cleaning.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum BookingErrorCode implements ErrorCode {

    INVALID_CLEANER_COUNT(1001, "Cleaner count must be 1, 2, or 3"),
    INVALID_DURATION(1002, "Duration must be exactly 2 or 4 hours"),
    INVALID_TIME_RANGE(1003, "Start time must be before end time"),
    NON_WORKING_DAY(1004, "Bookings cannot be made on Fridays"),
    OUTSIDE_WORKING_HOURS(1005, "Booking must be between 08:00 and 22:00");

    private final int code;
    private final String message;

}
