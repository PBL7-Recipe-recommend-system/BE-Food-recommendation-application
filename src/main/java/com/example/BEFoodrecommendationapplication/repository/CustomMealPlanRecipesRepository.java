package com.example.BEFoodrecommendationapplication.repository;

import com.example.BEFoodrecommendationapplication.entity.CustomMealPlan;
import com.example.BEFoodrecommendationapplication.entity.CustomMealPlanRecipes;
import com.example.BEFoodrecommendationapplication.entity.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface CustomMealPlanRecipesRepository extends JpaRepository<CustomMealPlanRecipes, Integer> {


    CustomMealPlanRecipes findByCustomMealPlanAndRecipeIdAndMealType(CustomMealPlan customMealPlan, Integer recipeId, MealType mealType);

    boolean existsByCustomMealPlanAndRecipeIdAndMealType(CustomMealPlan customMealPlan, Integer recipeId, MealType mealType);
}