package com.example.idatt2106_2022_05_backend.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "pictures")
public class Picture {

    @Id
    @SequenceGenerator(name = "picture_sequence", sequenceName = "picture_sequence", allocationSize = 1)
    @GeneratedValue(generator = "picture_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "picture_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String filename;

    @Column(name = "type")
    private String type;

    @Lob // LOB is datatype for storing large object data
    @Column(name = "data", nullable = false)
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Picture picture = (Picture) o;
        return id != null && Objects.equals(id, picture.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
