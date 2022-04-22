package com.example.idatt2106_2022_05_backend.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public class CalendarDto {

    // AdId
    private long adId;

    // Date from
    private LocalDate startDate;

    // Date to
    private LocalDate endDate;

    // Available (for use in marking dates as available/unavailable)
    private boolean available;


    // GETTERS
    public long getAdId() {
        return adId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isAvailable() {
        return available;
    }


    // SETTERS
    public void setAdId(long adId) {
        this.adId = adId;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
