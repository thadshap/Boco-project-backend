package com.example.idatt2106_2022_05_backend.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data

public class Message {


    private String from;

    private String text;
}
