package com.justlife.home.cleaning.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.justlife.home.cleaning.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private Integer durationHours;        // 2 or 4
    private Integer requiredCleanerCount; // 1, 2, or 3

    private String customerName;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @ManyToMany
    @JoinTable(
            name = "booking_cleaner",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "cleaner_id")
    )
    @JsonIgnore
    private List<Cleaner> cleaners = new ArrayList<>();
}
