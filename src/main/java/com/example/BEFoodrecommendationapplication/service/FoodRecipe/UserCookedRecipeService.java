package com.example.BEFoodrecommendationapplication.service.FoodRecipe;

import com.example.BEFoodrecommendationapplication.dto.DailyNutritionResponse;

import java.time.LocalDate;

public interface UserCookedRecipeService {

    public DailyNutritionResponse getDailyNutrition(Integer userId, LocalDate date);
}
