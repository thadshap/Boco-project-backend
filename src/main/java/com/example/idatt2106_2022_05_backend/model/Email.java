package com.example.idatt2106_2022_05_backend.model;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * A class that represents an Email object
 */
@Getter
@Setter
@AllArgsConstructor
@Data
@Builder
public class Email {

    @NotNull
    private String from;

    @NotNull
    private String to;

    private String subject;

    @NotNull
    private ThymeleafTemplate template;
}
