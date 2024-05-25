package com.example.BEFoodrecommendationapplication.service.MealPlan;

import com.example.BEFoodrecommendationapplication.dto.MealPlanDto;
import com.example.BEFoodrecommendationapplication.dto.MealPlanInput;
import com.example.BEFoodrecommendationapplication.entity.MealPlan;

import java.util.List;

public interface MealPlanService  {
    List<MealPlanDto> editMealPlans(List<MealPlanInput> mealPlansDto, int userId);
    MealPlanInput addMealPlans(MealPlanInput mealPlanInput, int userId);
    List<MealPlanDto> getCurrentMealPlans(Integer userId);
}
