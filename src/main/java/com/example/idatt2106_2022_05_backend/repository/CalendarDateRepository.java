package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.Ad;
import com.example.idatt2106_2022_05_backend.model.CalendarDate;
import com.example.idatt2106_2022_05_backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface CalendarDateRepository extends JpaRepository<CalendarDate, Long> {

    // Get all dates between date 1 and date 2
    Set<CalendarDate> findByDateBetween(LocalDate date1, LocalDate date2);


    // Get all available dates between for current month
    // Get all dates left in current month

}
