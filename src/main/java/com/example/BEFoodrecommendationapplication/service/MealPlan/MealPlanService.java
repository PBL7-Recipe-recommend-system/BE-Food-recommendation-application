package com.example.BEFoodrecommendationapplication.service.MealPlan;

import com.example.BEFoodrecommendationapplication.dto.MealPlanDto;
import com.example.BEFoodrecommendationapplication.dto.MealPlanInput;
import com.example.BEFoodrecommendationapplication.entity.MealPlan;

import java.util.List;

public interface MealPlanService  {
    List<MealPlanDto> addMealPlans(List<MealPlanInput> mealPlansDto, int userId);
}
