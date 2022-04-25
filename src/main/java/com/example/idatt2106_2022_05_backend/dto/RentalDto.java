package com.example.idatt2106_2022_05_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel(description = "Data transfer object for rental of item")
public class RentalDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd")
    @ApiModelProperty(notes = "date of agreement")
    private LocalDate dateOfRental;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    @ApiModelProperty(notes = "start of rental")
    private LocalDate rentFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    @ApiModelProperty(notes = "end of rental")
    private LocalDate rentTo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    @ApiModelProperty(notes = "deadline of cancellation of rental")
    private LocalDate deadline;

    @ApiModelProperty(notes = "id of owner of item")
    private Long owner;

    @ApiModelProperty(notes = "id of user that rents item")
    private Long borrower;

    @ApiModelProperty(notes = "id of ad")
    private Long ad;
}
