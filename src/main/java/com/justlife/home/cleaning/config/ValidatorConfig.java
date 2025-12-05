package com.justlife.home.cleaning.config;

import com.justlife.home.cleaning.validation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for building the validation chain used
 * in booking operations.
 */
@Configuration
public class ValidatorConfig {

    /**
     * Constructs a complete validation chain for booking requests.
     */
    @Bean
    public BookingValidator bookingValidator() {

        BookingValidator nonWorkingDayValidator = new NonWorkingDayValidator();
        BookingValidator workingHoursValidator = new WorkingHoursValidator();
        BookingValidator durationValidator = new DurationValidator();
        BookingValidator cleanerCountValidator = new CleanerCountValidator();
        BookingValidator pastDateValidator = new PastDateValidator();

        // Build chain (order matters) chain of resposibility
        nonWorkingDayValidator
                .linkWith(pastDateValidator)
                .linkWith(workingHoursValidator)
                .linkWith(durationValidator)
                .linkWith(cleanerCountValidator);

        return nonWorkingDayValidator;
    }
}
