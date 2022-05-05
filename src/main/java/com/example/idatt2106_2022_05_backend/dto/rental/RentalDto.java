package com.example.idatt2106_2022_05_backend.dto.rental;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Data transfer object for rental of item")
public class RentalDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(notes = "date of agreement")
    private LocalDate dateOfRental;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(notes = "start of rental")
    private LocalDate rentFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(notes = "end of rental")
    private LocalDate rentTo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ApiModelProperty(notes = "deadline of cancellation of rental")
    private LocalDate deadline;

    private boolean active;

    private int price;

    private boolean isReviewed;

    @ApiModelProperty(notes = "name of owner of item")
    private String owner;

    @ApiModelProperty(notes = "name of user that rents item")
    private String borrower;

    private String title;

    @ApiModelProperty(notes = "id of ad")
    private Long adId;

    @ApiModelProperty(notes = "id of rental")
    private Long id;
}
