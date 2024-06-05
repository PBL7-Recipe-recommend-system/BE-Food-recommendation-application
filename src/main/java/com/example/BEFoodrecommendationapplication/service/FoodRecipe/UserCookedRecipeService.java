package com.example.BEFoodrecommendationapplication.service.FoodRecipe;

import java.time.LocalDate;
import java.util.Map;

public interface UserCookedRecipeService {

    Map<String, Float> getDailyNutrition(Integer userId, LocalDate date);
}
