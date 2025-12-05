package com.justlife.home.cleaning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class TimeWindowDTO {
    @Schema(description = "Available start time", example = "08:00")
    private LocalTime start;

    @Schema(description = "Available end time", example = "10:00")
    private LocalTime end;
}