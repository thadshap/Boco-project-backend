package com.example.idatt2106_2022_05_backend.service.calendar;

import com.example.idatt2106_2022_05_backend.dto.CalendarDto;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

@Service
public interface CalendarService {
    Response markDatesFromToAs(CalendarDto dto);

    Response getUnavailableDates(CalendarDto dto);
}
