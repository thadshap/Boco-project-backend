package com.example.idatt2106_2022_05_backend.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pictures")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "picture_id", nullable = false)
    private Long pictureId;

    @Column(name = "name", nullable = false)
    private String filename;

    @Column(name="type")
    private String type;

    @Column(name="content")
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

}
