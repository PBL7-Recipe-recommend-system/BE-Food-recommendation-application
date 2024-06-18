package com.example.BEFoodrecommendationapplication.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "custom_meal_plan")
public class CustomMealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_meal_plan_id")
    private Integer customMealPlanId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "daily_calorie")
    private Integer dailyCalories;

    @Column(name = "total_calories")
    private Integer totalCalories;

    @Column(name = "description")
    private String description;

    @Column(name = "meal_count")
    private Integer mealCount;

    @OneToMany(mappedBy = "customMealPlan")
    private Set<CustomMealPlanRecipes> customMealPlanRecipes;


}
