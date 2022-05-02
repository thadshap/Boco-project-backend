package com.example.idatt2106_2022_05_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ThymeleafTemplate {
    private String template;
    private Map<String, Object> variables;
}
