package com.example.BEFoodrecommendationapplication.service.FoodRecipe;


import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.entity.UserCookedRecipe;
import com.example.BEFoodrecommendationapplication.entity.WaterIntake;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.UserCookedRecipeRepository;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import com.example.BEFoodrecommendationapplication.repository.WaterIntakeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCookedRecipeServiceImpl implements UserCookedRecipeService {


    private final UserCookedRecipeRepository userCookedRecipeRepository;

    private final WaterIntakeRepository waterIntakeRepository;


    private final UserRepository userRepository;

    public float calculateDailyWaterIntake(User user) {
        float waterIntakePerKg = 35; // milliliters of water per kilogram of body weight
        float weight = user.getWeight();
        float baseWaterIntake = weight * waterIntakePerKg; // Basic water intake in milliliters

        // Convert milliliters to liters
        float baseWaterIntakeLiters = baseWaterIntake / 1000;

        // Adjust for activity level
        String activityLevel = user.getDailyActivities();
        switch (activityLevel) {
            case "Little/no exercise":
                baseWaterIntakeLiters *= 1.0F; // No change for minimal activity
                break;
            case "Light exercise":
                baseWaterIntakeLiters *= 1.1F; // Increase by 10%
                break;
            case "Moderate exercise (3-5 days/wk)":
                baseWaterIntakeLiters *= 1.2F; // Increase by 20%
                break;
            case "Very active (6-7 days/wk)":
                baseWaterIntakeLiters *= 1.5F; // Increase by 50%
                break;
            case "Extra active (very active & physical job)":
                baseWaterIntakeLiters *= 1.75F; // Increase by 75%
                break;
        }

        return baseWaterIntakeLiters;
    }


    @Override
    public Map<String, Float> getDailyNutrition(Integer userId, LocalDate date) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));
        List<UserCookedRecipe> cookedRecipes = userCookedRecipeRepository.findByUserAndDate(user, date);

        float totalCalories = 0;
        float totalFatCalories = 0;
        float totalProteinCalories = 0;
        float totalCarbohydrateCalories = 0;
        float totalFatContent = 0;
        float totalSodiumContent = 0;
        float totalCarbohydrateContent = 0;
        float totalFiberContent = 0;
        float totalSugarContent = 0;
        float totalProteinContent = 0;

        float dailyWaterIntakeRecommendation = calculateDailyWaterIntake(user);

        for (UserCookedRecipe cookedRecipe : cookedRecipes) {
            totalCalories += cookedRecipe.getRecipe().getCalories();
            totalFatCalories += cookedRecipe.getRecipe().getFatContent() * 9;
            totalProteinCalories += cookedRecipe.getRecipe().getProteinContent() * 4;
            totalCarbohydrateCalories += cookedRecipe.getRecipe().getCarbonhydrateContent() * 4;
            totalFatContent += cookedRecipe.getRecipe().getFatContent();
            totalSodiumContent += cookedRecipe.getRecipe().getSodiumContent();
            totalCarbohydrateContent += cookedRecipe.getRecipe().getCarbonhydrateContent();
            totalFiberContent += cookedRecipe.getRecipe().getFiberContent();
            totalSugarContent += cookedRecipe.getRecipe().getSugarContent();
            totalProteinContent += cookedRecipe.getRecipe().getProteinContent();
        }

        Optional<WaterIntake> waterIntake = waterIntakeRepository.findByUserIdAndDate(userId, date);
        float waterIntakeAmount;
        if (waterIntake.isEmpty()) {
            waterIntakeAmount = 0;
        } else {
            waterIntakeAmount = waterIntake.get().getAmount();
        }

        Map<String, Float> nutrition = new HashMap<>();
        nutrition.put("recommendedWaterIntake", dailyWaterIntakeRecommendation);
        nutrition.put("waterIntake", waterIntakeAmount);
        nutrition.put("calories", totalCalories);
        nutrition.put("fatContent", totalFatContent);
        nutrition.put("sodiumContent", totalSodiumContent);
        nutrition.put("carbohydrateContent", totalCarbohydrateContent);
        nutrition.put("fiberContent", totalFiberContent);
        nutrition.put("sugarContent", totalSugarContent);
        nutrition.put("proteinContent", totalProteinContent);


        if (totalCarbohydrateContent > 0) {
            nutrition.put("fiberPercentage", (totalFiberContent / totalCarbohydrateContent) * 100);
            nutrition.put("sugarPercentage", (totalSugarContent / totalCarbohydrateContent) * 100);
        } else {
            nutrition.put("fiberPercentage", 0f);
            nutrition.put("sugarPercentage", 0f);
        }

        if (totalCalories > 0) {
            nutrition.put("fatPercentage", (totalFatCalories / totalCalories) * 100);
            nutrition.put("proteinPercentage", (totalProteinCalories / totalCalories) * 100);
            nutrition.put("carbohydratePercentage", (totalCarbohydrateCalories / totalCalories) * 100);
        } else {
            nutrition.put("fatPercentage", 0f);
            nutrition.put("proteinPercentage", 0f);
            nutrition.put("carbohydratePercentage", 0f);
        }

        return nutrition;
    }

}