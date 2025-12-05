package com.justlife.home.cleaning.exception;

import com.justlife.home.cleaning.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final int errorCode;

    public ResourceNotFoundException(ErrorCode error) {
        super(error.getMessage());
        this.errorCode = error.getCode();
    }
}
