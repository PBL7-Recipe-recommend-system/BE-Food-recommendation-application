package com.example.BEFoodrecommendationapplication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;


@Entity
@Table(name = "meal_plans")
@Getter
@Setter
@ToString
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToOne
    @JoinColumn(name = "breakfast_id")
    private FoodRecipe breakfast;

    @OneToOne
    @JoinColumn(name = "lunch_id")
    private FoodRecipe lunch;

    @OneToOne
    @JoinColumn(name = "dinner_id")
    private FoodRecipe dinner;

    @OneToOne
    @JoinColumn(name = "morning_snack_id")
    private FoodRecipe morningSnack;

    @OneToOne
    @JoinColumn(name = "afternoon_snack_id")
    private FoodRecipe afternoonSnack;

    @Column(name = "meal_count")
    private Integer mealCount ;

    @Column(name = "date", nullable = false)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @Column(name = "daily_calories")
    private Integer dailyCalories;

    @Column(name = "total_calories")
    private Integer totalCalories;

    @Column(name = "description")
    private String description;

}
