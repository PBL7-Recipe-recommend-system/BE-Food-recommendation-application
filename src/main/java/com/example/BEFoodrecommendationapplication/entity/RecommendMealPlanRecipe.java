package com.example.BEFoodrecommendationapplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

enum MealType {
    breakfast,
    lunch,
    dinner,
    morningSnack,
    afternoonSnack
}

@Entity
@Getter
@Setter
@Table(name = "recommend_meal_plan_recipes")
public class RecommendMealPlanRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "recommend_meal_plan_id", nullable = false)
    private RecommendMealPlan recommendMealPlan;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private FoodRecipe recipe;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "is_cook", nullable = false)
    private Boolean isCook;

}