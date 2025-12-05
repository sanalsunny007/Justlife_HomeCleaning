package com.justlife.home.cleaning.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.justlife.home.cleaning.constants.ApplicationConstants;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cleaner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalTime workStart = ApplicationConstants.WORK_START;
    private LocalTime workEnd   = ApplicationConstants.WORK_END;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    @JsonIgnore
    private Vehicle vehicle;

    @ManyToMany(mappedBy = "cleaners")
    @JsonIgnore
    private List<Booking> bookings = new ArrayList<>();
}
