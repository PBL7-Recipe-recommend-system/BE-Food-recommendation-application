package com.example.BEFoodrecommendationapplication.service.MealPlan;

import com.example.BEFoodrecommendationapplication.dto.AddRecipeMealPlanInput;
import com.example.BEFoodrecommendationapplication.dto.CustomMealPlanDto;
import com.example.BEFoodrecommendationapplication.dto.CustomMealPlanInput;
import com.example.BEFoodrecommendationapplication.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface MealPlanService {

    void deleteUserMealPlans(User user);


    void addCustomMealPlan(CustomMealPlanInput customMealPlanInput, Integer userId);

    CustomMealPlanDto editCustomMealPlan(CustomMealPlanInput customMealPlanInput, Integer userId);

    List<CustomMealPlanDto> getCustomMealPlans(Integer userId);

    void addRecipeToMealPlan(AddRecipeMealPlanInput input, Integer userId);

    void removeRecipeFromMealPlan(AddRecipeMealPlanInput input, Integer userId);

    void editMealPlanDescription(Integer userId, LocalDate date, Integer dailyCalo, String description);
}
