package com.example.idatt2106_2022_05_backend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel(description = "Data transfer object for rental of item")
public class RentalDto {

    @ApiModelProperty(notes = "date of agreement")
    private LocalDate dateOfRental;

    @ApiModelProperty(notes = "start of rental")
    private LocalDate rentFrom;

    @ApiModelProperty(notes = "end of rental")
    private LocalDate rentTo;

    @ApiModelProperty(notes = "deadline of cancellation of rental")
    private LocalDate deadline;

    @ApiModelProperty(notes = "id of owner of item")
    private long owner;

    @ApiModelProperty(notes = "id of user that rents item")
    private long borrower;

    @ApiModelProperty(notes = "id of ad")
    private long ad;
}
