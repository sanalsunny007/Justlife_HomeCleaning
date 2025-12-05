package com.justlife.home.cleaning.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;


@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CleanerAvailabilityDTO extends CleanerDTO {

    @Schema(description = "List of free time slots available for the cleaner")
    private List<TimeWindowDTO> availableWindows;
}