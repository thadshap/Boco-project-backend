package com.example.idatt2106_2022_05_backend.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    private boolean parent;

//    @NotNull
//    private boolean middleChild;

    @NotNull
    private boolean child;

    private int level;

    private String parentName;

    private String icon;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "category")
    private Set<Ad> ads;

    @PreRemove
    private void removeRelationships() {
        if (ads != null) {
            setAds(null);
        }
    }

    @Override
    public String toString() {
        return "Category{" + "id=" + id + ", name='" + name + '\'' + ", parent=" + parent + ", parentName='"
                + parentName + '\'' + ", ads=" + ads + '}';
    }
}
