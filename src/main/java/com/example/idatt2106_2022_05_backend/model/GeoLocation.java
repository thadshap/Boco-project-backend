package com.example.idatt2106_2022_05_backend.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class GeoLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="GEO_ID", nullable=false)
    private Long geo_id;

    @Column(name="LAT")
    private double lat;

    @Column(name="LNG")
    private  double lng;

    //TODO: add relation to AD

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GeoLocation that = (GeoLocation) o;
        return Objects.equals(geo_id, that.geo_id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
