package com.example.BEFoodrecommendationapplication.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "custom_meal_plan_recipes")
public class CustomMealPlanRecipes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "custom_meal_plan_id", referencedColumnName = "custom_meal_plan_id")
    private CustomMealPlan customMealPlan;

    @Column(name = "recipe_id")
    private Integer recipeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    private MealType mealType;


}


