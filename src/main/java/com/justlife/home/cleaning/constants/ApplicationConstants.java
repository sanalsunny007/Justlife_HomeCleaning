package com.justlife.home.cleaning.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstants {
    public static final int BREAK_MINUTES = 30;
    public static final LocalTime WORK_START = LocalTime.of(8, 0);
    public static final LocalTime WORK_END   = LocalTime.of(22, 0);
}
