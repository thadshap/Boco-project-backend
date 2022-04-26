package com.example.idatt2106_2022_05_backend.dto.rental;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalUpdateDto {

    private LocalDate rentFrom;

    private LocalDate rentTo;

    private LocalDate deadline;

    private int price;
}
