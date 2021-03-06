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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "picture_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String filename;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "image")
    private String base64;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @OneToOne()
    private User user;

    @PreRemove
    private void removeRelationships() {
        if (user != null) {
            setUser(null);
        }
        if (ad != null) {
            setAd(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Picture picture = (Picture) o;
        return id != null && Objects.equals(id, picture.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
