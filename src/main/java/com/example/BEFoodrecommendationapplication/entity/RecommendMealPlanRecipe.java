package com.example.BEFoodrecommendationapplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
public class RecommendMealPlanRecipe implements Serializable {

    @EmbeddedId
    private RecommendMealPlanRecipeId id;
    @Column(name = "is_cook", nullable = false)
    private Boolean isCook;
    @MapsId("recommendMealPlanId")
    @ManyToOne
    @JoinColumn(name = "recommend_meal_plan_id", insertable = false, updatable = false)
    private RecommendMealPlan recommendMealPlan;
    @MapsId("recipeId")
    @ManyToOne
    @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
    private FoodRecipe recipe;

    @Embeddable
    public static class RecommendMealPlanRecipeId implements Serializable {
        @Column(name = "recommend_meal_plan_id")
        private Integer recommendMealPlanId;

        @Column(name = "recipe_id")
        private Integer recipeId;

        @Enumerated(EnumType.STRING)
        @Column(name = "meal_type")
        private MealType mealType;


    }
}