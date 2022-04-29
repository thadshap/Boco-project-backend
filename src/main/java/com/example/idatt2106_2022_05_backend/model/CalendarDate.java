package com.example.idatt2106_2022_05_backend.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
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
    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    // Is nullable
    @Column(name = "available")
    private boolean available;

    // Many-to-many connection with Ad. Ad is parent in this case.
    @ManyToMany(fetch = FetchType.EAGER,  mappedBy = "dates")
    @ToString.Exclude
    private Set<Ad> ads = new HashSet<>();

}
