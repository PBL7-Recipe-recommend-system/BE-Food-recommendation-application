package com.example.BEFoodrecommendationapplication.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ingredient")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    @CsvBindByName(column = "ingredient_id")
    private int id;

    @Column(name = "name", nullable = false, length = 100)
    @CsvBindByName(column = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    @CsvBindByName(column = "Description")
    private String description;

    @OneToMany(mappedBy = "ingredient")
    @JsonIgnore
    private Set<UserIncludeIngredient> includeUsers;

    @OneToMany(mappedBy = "ingredient")
    @JsonIgnore
    private Set<UserExcludeIngredient> excludeUsers;
}