package com.justlife.home.cleaning.repository;

import com.justlife.home.cleaning.entity.Booking;
import com.justlife.home.cleaning.entity.Cleaner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b " +
            "where date(b.startDateTime) = :date and b.status = 'CONFIRMED'")
    List<Booking> findByDate(LocalDate date);

    @Query("select b from Booking b join b.cleaners c " +
            "where c = :cleaner and date(b.startDateTime) = :date and b.status = 'CONFIRMED'")
    List<Booking> findByCleanerAndDate(Cleaner cleaner, LocalDate date);

}
