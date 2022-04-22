package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.CalendarDto;
import com.example.idatt2106_2022_05_backend.service.calendar.CalendarService;
import com.example.idatt2106_2022_05_backend.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    CalendarService calendarService;


    // sends all unavailable dates for the remaining span of the ad
    // dto contains adId
    @PostMapping()
    public Response getUnavailableDates(@RequestBody CalendarDto dto) {
        return calendarService.getUnavailableDates(dto);
    }

    // the frontend sends two dates that an item is to be rented/cancelled out to from
    // dto contains date from, date to, ad id and available (true for rental, false for cancellation)


    /**
     * Method marks a selected span of dates (for specified ad) as available.
     * Available is either true or false.
     *
     * @param dto contains:
     *            date from
     *            date to
     *            adId
     *            available (true for rental, false for cancellation)
     * @return
     */
    @PostMapping()
    public Response postRentalDates(@RequestBody CalendarDto dto) {
        return calendarService.markDatesFromToAs(dto);
    }
}
