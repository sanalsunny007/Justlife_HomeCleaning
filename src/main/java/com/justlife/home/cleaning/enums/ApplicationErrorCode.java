package com.justlife.home.cleaning.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationErrorCode implements ErrorCode {
    BOOKING_NOT_FOUND(2001, "Booking not found"),
    VALIDATION_FAILED(40001, "Validation failed"),
    INTERNAL_ERROR(50001, "Internal server error"),

    NO_CLEANERS_AVAILABLE(3001, "No cleaners available for the selected time slot"),
    INSUFFICIENT_CLEANERS_FOR_UPDATE(3002, "Not enough cleaners available to update booking"),
    NO_VEHICLE_WITH_REQUIRED_CLEANERS(3003, "No vehicle has enough available cleaners for this booking"),
    PAST_DATE_NOT_ALLOWED(3004, "Cannot check availability for past dates");;

    private final int code;
    private final String message;
}
