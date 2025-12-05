package com.justlife.home.cleaning.validation;

import java.time.LocalDateTime;

public abstract class AbstractBookingValidator implements BookingValidator {

    private BookingValidator next;

    public BookingValidator linkWith(BookingValidator next) {
        this.next = next;
        return next;
    }

    @Override
    public void validate(LocalDateTime start, LocalDateTime end, int cleanerCount) {
        check(start, end, cleanerCount);
        if (next != null) {
            next.validate(start, end, cleanerCount);
        }
    }

    protected abstract void check(LocalDateTime start, LocalDateTime end, int cleanerCount);
}