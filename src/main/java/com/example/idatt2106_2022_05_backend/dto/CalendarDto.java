package com.example.idatt2106_2022_05_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@ApiModel(description = "Data transfer object of calender dates, used to send and return calender dates")
public class CalendarDto {

    @ApiModelProperty(notes = "id of corresponding ad")
    private long adId;

    @ApiModelProperty(notes = "ad of rental, needed for cancelling a rental")
    private long rentalId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(notes = "start date of calendar")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(notes = "end date of calendar")
    private LocalDate endDate;

    // Available (for use in marking dates as available/unavailable)
    @ApiModelProperty(notes = "true if dates are available")
    private boolean available;

}
