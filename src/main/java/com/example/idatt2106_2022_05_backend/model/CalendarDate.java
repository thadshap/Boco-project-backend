package com.example.idatt2106_2022_05_backend.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "dates")
public class CalendarDate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "date_id", nullable = false)
    private Long dateId;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "timeZone", nullable = false) // todo needed? or maybe better for use in service methods?
    private ZoneId timezone;

    // Is nullable
    @Column(name = "available")
    private boolean available;

    // Many to many connection to ads modelled by the "calendar" table (not modelled)
    @ManyToMany()
    @JoinTable(
            name = "calendar",
            joinColumns = { @JoinColumn(name = "date_id") },
            inverseJoinColumns = { @JoinColumn(name = "ad_id") }
    )
    private Set<Ad> ads;
}
