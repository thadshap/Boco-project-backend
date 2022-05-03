package com.example.idatt2106_2022_05_backend.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "messages")
public class OutputMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_id", nullable = false)
    private Long id;

    @Column(name = "sender")
    private String from;

    @Column(name = "text")
    private String text;

    @Column(name = "time")
    private String time;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
