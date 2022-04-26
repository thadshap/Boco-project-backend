package com.example.idatt2106_2022_05_backend.service.calendar;

import com.example.idatt2106_2022_05_backend.dto.CalendarDto;
import com.example.idatt2106_2022_05_backend.model.CalendarDate;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface CalendarService {
    Response markDatesFromToAs(CalendarDto dto);

    Set<CalendarDate> addFutureDates(long adId);

    Response getUnavailableDates(CalendarDto dto);
}
