package com.justlife.home.cleaning.utils;

import com.justlife.home.cleaning.dto.CleanerDTO;
import com.justlife.home.cleaning.entity.Cleaner;
import com.justlife.home.cleaning.entity.Vehicle;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CleanerMapper {

    public static CleanerDTO toResponse(Cleaner cleaner) {

        return CleanerDTO.builder()
                .cleanerId(cleaner.getId())
                .cleanerName(cleaner.getName())
                .vehicleId(cleaner.getVehicle().getId())
                .vehicleName(cleaner.getVehicle().getName())
                .build();
    }

    public static Cleaner toEntity(CleanerDTO dto) {
        if (dto == null) return null;

        Cleaner cleaner = new Cleaner();
        cleaner.setId(dto.getCleanerId());
        cleaner.setName(dto.getCleanerName());

        Vehicle v = new Vehicle();
        v.setId(dto.getVehicleId());
        v.setName(dto.getVehicleName());
        cleaner.setVehicle(v);

        return cleaner;
    }

    public static List<Cleaner> toEntityList(List<CleanerDTO> dtos) {
        return dtos.stream().map(CleanerMapper::toEntity).toList();
    }
}
