package com.justlife.home.cleaning.exception;

import com.justlife.home.cleaning.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BookingValidationException extends RuntimeException {

    private final int errorCode;

    public BookingValidationException(ErrorCode error) {
        super(error.getMessage());
        this.errorCode = error.getCode();
    }
}
