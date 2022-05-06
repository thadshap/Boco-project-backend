package com.example.idatt2106_2022_05_backend.dto.rental;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Data transfer object for updating a rental")
public class RentalUpdateDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Nullable
    @ApiModelProperty(notes = "start of rental")
    private LocalDate rentFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Nullable
    @ApiModelProperty(notes = "end of rental")
    private LocalDate rentTo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Nullable
    @ApiModelProperty(notes = "deadline of cancellation of rental")
    private LocalDate deadline;

    @Nullable
    private int price;
}
