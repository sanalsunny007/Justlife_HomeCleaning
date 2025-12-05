package com.justlife.home.cleaning.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CleanerDTO {
    @Schema(description = "Cleaner ID", example = "12")
    private Long cleanerId;

    @Schema(description = "Cleaner name", example = "Cleaner-1-3")
    private String cleanerName;

    @Schema(description = "Vehicle ID", example = "1")
    private Long vehicleId;

    @Schema(description = "Vehicle name", example = "Vehicle-1")
    private String vehicleName;

}