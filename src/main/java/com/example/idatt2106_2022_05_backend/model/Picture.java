package com.example.idatt2106_2022_05_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pictures")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "picture_id", nullable = false)
    private Long pictureId;

    @Column(name = "content", nullable = false)
    private String title;

    @JoinColumn(name = "ad_id")
    private Ad ad;

}
