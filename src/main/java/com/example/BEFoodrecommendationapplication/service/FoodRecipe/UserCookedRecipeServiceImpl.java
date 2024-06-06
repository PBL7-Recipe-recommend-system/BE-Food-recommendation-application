package com.example.BEFoodrecommendationapplication.service.FoodRecipe;


import com.example.BEFoodrecommendationapplication.dto.DailyNutritionResponse;
import com.example.BEFoodrecommendationapplication.dto.MealNutrition;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
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
import java.util.List;
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
    public DailyNutritionResponse getDailyNutrition(Integer userId, LocalDate date) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));
        List<UserCookedRecipe> cookedRecipes = userCookedRecipeRepository.findByUserAndDate(user, date);

        DailyNutritionResponse response = new DailyNutritionResponse();


        float dailyWaterIntakeRecommendation = calculateDailyWaterIntake(user);

        Optional<WaterIntake> waterIntake = waterIntakeRepository.findByUserIdAndDate(userId, date);
        float waterIntakeAmount;
        if (waterIntake.isEmpty()) {
            waterIntakeAmount = 0;
        } else {
            waterIntakeAmount = waterIntake.get().getAmount();
        }
        response.setRecommendWaterIntake(dailyWaterIntakeRecommendation);
        response.setWaterIntake(waterIntakeAmount);

        for (UserCookedRecipe cookedRecipe : cookedRecipes) {
            String meal = cookedRecipe.getMeal();
            FoodRecipe recipe = cookedRecipe.getRecipe();

            MealNutrition nutrition = response.getMeals().getOrDefault(meal, new MealNutrition());
            // Initialize with recipe details if not already set
            if (nutrition.getRecipeId() == null) {
                nutrition.setRecipeId(recipe.getRecipeId());
                nutrition.setName(recipe.getName());
                nutrition.setServings(cookedRecipe.getServingSize());
            }

            // Aggregate nutritional values
            aggregateNutrition(nutrition, recipe);

            // Compute percentages
            computeNutrientPercentages(nutrition);

            // Update the response
            response.addMealNutrition(meal, nutrition);
        }

        return response;
    }

    private void aggregateNutrition(MealNutrition nutrition, FoodRecipe recipe) {
        nutrition.setCalories(optionalSum(nutrition.getCalories(), recipe.getCalories()));
        nutrition.setFatContent(optionalSum(nutrition.getFatContent(), recipe.getFatContent()));
        nutrition.setSodiumContent(optionalSum(nutrition.getSodiumContent(), recipe.getSodiumContent()));
        nutrition.setCarbohydrateContent(optionalSum(nutrition.getCarbohydrateContent(), recipe.getCarbonhydrateContent()));
        nutrition.setFiberContent(optionalSum(nutrition.getFiberContent(), recipe.getFiberContent()));
        nutrition.setSugarContent(optionalSum(nutrition.getSugarContent(), recipe.getSugarContent()));
        nutrition.setProteinContent(optionalSum(nutrition.getProteinContent(), recipe.getProteinContent()));
    }

    private Float optionalSum(Float a, Float b) {
        if (a == null) a = 0f;
        if (b == null) b = 0f;
        return a + b;
    }

    private void computeNutrientPercentages(MealNutrition nutrition) {
        nutrition.setFiberPercentage(computePercentage(nutrition.getFiberContent(), nutrition.getCarbohydrateContent()));
        nutrition.setSugarPercentage(computePercentage(nutrition.getSugarContent(), nutrition.getCarbohydrateContent()));
        nutrition.setFatPercentage(computePercentage(nutrition.getFatContent(), nutrition.getCalories(), 9));
        nutrition.setProteinPercentage(computePercentage(nutrition.getProteinContent(), nutrition.getCalories(), 4));
        nutrition.setCarbohydratePercentage(computePercentage(nutrition.getCarbohydrateContent(), nutrition.getCalories(), 4));
    }

    private Float computePercentage(Float nutrientValue, Float totalValue) {
        return (totalValue != null && totalValue > 0) ? (nutrientValue / totalValue) * 100 : 0;
    }

    private Float computePercentage(Float nutrientValue, Float totalCalories, int calorieFactor) {
        return (totalCalories != null && totalCalories > 0) ? (nutrientValue * calorieFactor / totalCalories) * 100 : 0;
    }


}