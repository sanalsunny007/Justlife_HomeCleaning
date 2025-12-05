package com.justlife.home.cleaning.utils;

import com.justlife.home.cleaning.constants.ApplicationConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtils {

    /**
     * Checks if the date falls on Friday
     *
     */
    public static boolean isNonWorkingDay(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.FRIDAY;
    }

    /**
     * Checks if the booking time provided is of outside working hours
     *
     */
    public static boolean isOutsideWorkingHours(LocalDateTime start, LocalDateTime end) {
        // End must not spill into next day
        if (!start.toLocalDate().equals(end.toLocalDate())) {
            return true; // crosses midnight => invalid
        }

        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();

        return startTime.isBefore(ApplicationConstants.WORK_START)
                || endTime.isAfter(ApplicationConstants.WORK_END);
    }

    /**
     * Checks if the provided date is in the past
     *
     */
    public static boolean isPastDate(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }

    /**
     * Central rule for accepted booking durations.
     *
     */
    public static boolean isValidDuration(long hours) {
        return hours == 2 || hours == 4;
    }

}
