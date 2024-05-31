package com.example.BEFoodrecommendationapplication.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "user_cooked_recipes")
public class UserCookedRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private FoodRecipe recipe;

    @Column(name = "is_cooked")
    private Boolean isCooked;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "serving_size")
    private Integer servingSize;

    @Column(name = "meal")
    private String meal;
}
