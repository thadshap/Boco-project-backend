package com.example.idatt2106_2022_05_backend.controller;

import com.example.idatt2106_2022_05_backend.dto.CalendarDto;
import com.example.idatt2106_2022_05_backend.service.calendar.CalendarService;
import com.example.idatt2106_2022_05_backend.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/calendar")
@Api(tags = "Controller class to handle user")
public class CalendarController {

    @Autowired
    CalendarService calendarService;

    // sends all unavailable dates for the remaining span of the ad
    // dto contains adId
    @PostMapping("/get")
    @ApiOperation(value = "Endpoint to get al unavailable dates for an ad", response = Response.class)
    public Response getUnavailableDates(@RequestBody CalendarDto dto) {
        log.debug("[X] Call to get available dates for ad");
        return calendarService.getUnavailableDates(dto);
    }

    // the frontend sends two dates that an item is to be rented/cancelled out to from
    // dto contains date from, date to, ad id and available (true for rental, false for cancellation)
    @PostMapping("/post") //todo fix these paths hihi
    @ApiOperation(value = "Endpoint to create see if dates are available", response = Response.class)
    public Response postRentalDates(@RequestBody CalendarDto dto) {
        log.debug("[X] Call to get rental dates");
        return calendarService.markDatesFromToAs(dto);
    }
}
