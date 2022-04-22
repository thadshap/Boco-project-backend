package com.example.idatt2106_2022_05_backend.repository;

import com.example.idatt2106_2022_05_backend.model.CalendarDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarDateRepository extends JpaRepository<CalendarDate, Long> {

    // Get all dates between date 1 and date 2
    // Get all available dates between for current month
    // Get all dates left in current month

}
