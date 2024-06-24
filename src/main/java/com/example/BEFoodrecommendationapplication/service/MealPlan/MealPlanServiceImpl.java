package com.example.BEFoodrecommendationapplication.service.MealPlan;

import com.example.BEFoodrecommendationapplication.dto.AddRecipeMealPlanInput;
import com.example.BEFoodrecommendationapplication.dto.CustomMealPlanDto;
import com.example.BEFoodrecommendationapplication.dto.CustomMealPlanInput;
import com.example.BEFoodrecommendationapplication.entity.*;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.*;
import com.example.BEFoodrecommendationapplication.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService {
    private final UserRepository userRepository;
    private final FoodRecipeRepository foodRecipeRepository;
    private final StringUtil stringUtil;
    private final RecommendMealPlanRepository recommendMealPlanRepository;
    private final RecommendMealPlanRecipeRepository recommendMealPlanRecipeRepository;
    private final CustomMealPlanRepository customMealPlanRepository;
    private final CustomMealPlanRecipesRepository customMealPlanRecipesRepository;


    private Optional<User> checkUser(int userId) {
        Optional<User> user = userRepository.findById(userId);
        User temp;

        if (user.isPresent()) {
            if (!user.get().isCustomPlan()) {
                temp = user.get();
                temp.setCustomPlan(true);
                userRepository.save(temp);
            }

        } else {
            throw new RecordNotFoundException("User not found with id: " + userId);
        }
        return user;
    }


    @Transactional
    @Override
    public CustomMealPlanDto editCustomMealPlan(CustomMealPlanInput customMealPlanInput, Integer userId) {
        User user = checkUser(userId).get();
        CustomMealPlan customMealPlan = customMealPlanRepository.findByUserIdAndDate(userId, customMealPlanInput.getDate());

        if (customMealPlan == null) {
            customMealPlan = new CustomMealPlan();
            customMealPlan.setUserId(userId);
            customMealPlan.setDate(customMealPlanInput.getDate());
        }

        if (customMealPlanInput.getDescription() != null) {
            customMealPlan.setDescription(customMealPlanInput.getDescription());
        }
        if (customMealPlanInput.getDailyCalories() != null) {
            customMealPlan.setDailyCalories(customMealPlanInput.getDailyCalories());
        }

        int mealCount = 0;
        if (customMealPlanInput.getBreakfastDishIds() != null) mealCount++;
        if (customMealPlanInput.getLunchDishIds() != null) mealCount++;
        if (customMealPlanInput.getDinnerDishIds() != null) mealCount++;
        if (customMealPlanInput.getAfternoonSnackDishIds() != null) mealCount++;
        if (customMealPlanInput.getMorningSnackDishIds() != null) mealCount++;

        // Set the meal count in the CustomMealPlan
        customMealPlan.setMealCount(mealCount);

        // Save and flush CustomMealPlan first to ensure it has an ID for the foreign key reference
        customMealPlan = customMealPlanRepository.saveAndFlush(customMealPlan);

        List<CustomMealPlanRecipes> customMealPlanRecipesList = new ArrayList<>();

        // Handle adding meal plan recipes for different meals
        processMealPlanRecipes(customMealPlanInput.getBreakfastDishIds(), customMealPlan, customMealPlanRecipesList, MealType.breakfast);
        processMealPlanRecipes(customMealPlanInput.getLunchDishIds(), customMealPlan, customMealPlanRecipesList, MealType.lunch);
        processMealPlanRecipes(customMealPlanInput.getDinnerDishIds(), customMealPlan, customMealPlanRecipesList, MealType.dinner);
        processMealPlanRecipes(customMealPlanInput.getAfternoonSnackDishIds(), customMealPlan, customMealPlanRecipesList, MealType.afternoonSnack);
        processMealPlanRecipes(customMealPlanInput.getMorningSnackDishIds(), customMealPlan, customMealPlanRecipesList, MealType.morningSnack);

        // Calculate total calories from all the recipes in the meal plan
        double totalCalories = 0;
        for (CustomMealPlanRecipes recipe : customMealPlanRecipesList) {
            FoodRecipe foodRecipe = foodRecipeRepository.findById(recipe.getRecipeId()).get();
            totalCalories += foodRecipe.getCalories();
        }

        // Set the total calories in the CustomMealPlan
        customMealPlan.setTotalCalories((int) totalCalories);

        // Then save the CustomMealPlanRecipes
        customMealPlanRecipesRepository.saveAll(customMealPlanRecipesList);

        // Save the updated CustomMealPlan
        customMealPlanRepository.save(customMealPlan);

        return mapToDto(customMealPlan);
    }

    private void processMealPlanRecipes(List<Integer> dishIds, CustomMealPlan customMealPlan, List<CustomMealPlanRecipes> customMealPlanRecipesList, MealType mealType) {
        if (dishIds != null) {
            for (Integer dishId : dishIds) {
                if (!customMealPlanRecipesRepository.existsByCustomMealPlanAndRecipeIdAndMealType(customMealPlan, dishId, mealType)) {
                    CustomMealPlanRecipes customMealPlanRecipes = new CustomMealPlanRecipes();
                    customMealPlanRecipes.setCustomMealPlan(customMealPlan);
                    customMealPlanRecipes.setRecipeId(dishId);
                    customMealPlanRecipes.setMealType(mealType);
                    customMealPlanRecipesList.add(customMealPlanRecipes);
                }
            }
        }
    }

    @Override
    public void addRecipeToMealPlan(AddRecipeMealPlanInput input, Integer userId) {
        CustomMealPlan customMealPlan = customMealPlanRepository.findByUserIdAndDate(userId, input.getDate());

        if (customMealPlan == null) {
            customMealPlan = new CustomMealPlan();
            customMealPlan.setUserId(userId);
            customMealPlan.setDate(input.getDate());
            customMealPlan = customMealPlanRepository.save(customMealPlan);
        }

        // Find the recipe
        FoodRecipe foodRecipe = foodRecipeRepository.findById(input.getRecipeId()).get();

        // Check if a CustomMealPlanRecipes with the same CustomMealPlan, recipeId, and mealType already exists
        boolean exists = customMealPlanRecipesRepository.existsByCustomMealPlanAndRecipeIdAndMealType(customMealPlan, foodRecipe.getRecipeId(), MealType.valueOf(input.getMeal()));

        // If it doesn't exist, create a new CustomMealPlanRecipes object and add it to the meal plan
        if (!exists) {
            CustomMealPlanRecipes customMealPlanRecipes = new CustomMealPlanRecipes();
            customMealPlanRecipes.setCustomMealPlan(customMealPlan);
            customMealPlanRecipes.setRecipeId(foodRecipe.getRecipeId());
            customMealPlanRecipes.setMealType(MealType.valueOf(input.getMeal()));

            customMealPlan.getCustomMealPlanRecipes().add(customMealPlanRecipes);
            customMealPlanRecipesRepository.save(customMealPlanRecipes);
            customMealPlanRepository.save(customMealPlan);
        }
    }

    @Override
    @Transactional
    public void removeRecipeFromMealPlan(AddRecipeMealPlanInput input, Integer userId) {
        // Find the meal plan for the given date
        CustomMealPlan customMealPlan = customMealPlanRepository.findByUserIdAndDate(userId, input.getDate());

        if (customMealPlan != null) {

            FoodRecipe foodRecipe = foodRecipeRepository.findById(input.getRecipeId()).get();

            CustomMealPlanRecipes customMealPlanRecipes = customMealPlanRecipesRepository.findByCustomMealPlanAndRecipeIdAndMealType(customMealPlan, foodRecipe.getRecipeId(), MealType.valueOf(input.getMeal()));

            if (customMealPlanRecipes != null) {
                customMealPlan.getCustomMealPlanRecipes().remove(customMealPlanRecipes);
                customMealPlanRecipesRepository.delete(customMealPlanRecipes);
                customMealPlanRepository.save(customMealPlan);
            }
        }
    }

    @Override
    public void editMealPlanDescription(Integer userId, LocalDate date, Integer dailyCalo, String description) {
        CustomMealPlan customMealPlan = customMealPlanRepository.findByUserIdAndDate(userId, date);
        customMealPlan.setDescription(description);
        customMealPlan.setDailyCalories(dailyCalo);
        customMealPlanRepository.save(customMealPlan);
    }

    public int countDistinctMealTypes(CustomMealPlan input) {
        Set<MealType> distinctMealTypes = new HashSet<>();

        if (input.getCustomMealPlanRecipes() != null) {
            for (CustomMealPlanRecipes recipe : input.getCustomMealPlanRecipes()) {
                distinctMealTypes.add(recipe.getMealType());
            }
        }
        input.setMealCount(distinctMealTypes.size());
        customMealPlanRepository.save(input);
        return distinctMealTypes.size();
    }

    @Override
    public void addCustomMealPlan(CustomMealPlanInput customMealPlanInput, Integer userId) {
        User user = checkUser(userId).get();

        // Check if a CustomMealPlan with the given date already exists
        CustomMealPlan customMealPlan = customMealPlanRepository.findByUserIdAndDate(userId, customMealPlanInput.getDate());

        // If it doesn't exist, create a new one
        if (customMealPlan == null) {
            customMealPlan = new CustomMealPlan();
            customMealPlan.setUserId(userId);
            customMealPlan.setDate(customMealPlanInput.getDate());
        }

        customMealPlan.setDescription(customMealPlanInput.getDescription());
        customMealPlan.setDailyCalories(customMealPlanInput.getDailyCalories());

        List<CustomMealPlanRecipes> customMealPlanRecipesList = new ArrayList<>();
        int mealCount = 0;
        double totalCalories = 0;

        // Process each meal type
        List<List<Integer>> allMealDishIds = Arrays.asList(customMealPlanInput.getBreakfastDishIds(), customMealPlanInput.getLunchDishIds(), customMealPlanInput.getDinnerDishIds(), customMealPlanInput.getMorningSnackDishIds(), customMealPlanInput.getAfternoonSnackDishIds());
        List<MealType> allMealTypes = Arrays.asList(MealType.breakfast, MealType.lunch, MealType.dinner, MealType.morningSnack, MealType.afternoonSnack);

        for (int i = 0; i < allMealDishIds.size(); i++) {
            List<Integer> dishIds = allMealDishIds.get(i);
            MealType mealType = allMealTypes.get(i);

            if (dishIds != null) {
                mealCount++;
                for (Integer dishId : dishIds) {

                    CustomMealPlanRecipes customMealPlanRecipes = new CustomMealPlanRecipes();
                    customMealPlanRecipes.setCustomMealPlan(customMealPlan);
                    customMealPlanRecipes.setRecipeId(dishId);
                    customMealPlanRecipes.setMealType(mealType);
                    customMealPlanRecipesList.add(customMealPlanRecipes);

                    // Retrieve the recipe and add its calories to the total
                    FoodRecipe foodRecipe = foodRecipeRepository.findById(dishId).get();
                    totalCalories += foodRecipe.getCalories();

                }
            }
        }

        customMealPlan.setMealCount(mealCount);
        customMealPlan.setTotalCalories((int) totalCalories);
        customMealPlanRepository.saveAndFlush(customMealPlan);
        customMealPlanRecipesRepository.saveAllAndFlush(customMealPlanRecipesList);

    }

    @Override
    public List<CustomMealPlanDto> getCustomMealPlans(Integer userId) {
        if (userRepository.findById(userId).isPresent()) {
            User user = userRepository.findById(userId).get();
            if (!user.isCustomPlan()) {
                throw new RecordNotFoundException("User hasn't created meal plan ");
            }
        }
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysFromNow = today.plusDays(7);
        List<CustomMealPlan> customMealPlans = customMealPlanRepository.findAllByUserIdAndDateBetween(userId, today, sevenDaysFromNow);
        return customMealPlans.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public CustomMealPlanDto mapToDto(CustomMealPlan customMealPlan) {
        List<Object> breakfast = customMealPlan.getCustomMealPlanRecipes() != null ?
                customMealPlan.getCustomMealPlanRecipes().stream()
                        .filter(recipe -> recipe.getMealType() == MealType.breakfast)
                        .map(recipe -> stringUtil.mapToShortRecipe(recipe.getRecipeId()))
                        .collect(Collectors.toList()) : new ArrayList<>();

        List<Object> lunch = customMealPlan.getCustomMealPlanRecipes() != null ?
                customMealPlan.getCustomMealPlanRecipes().stream()
                        .filter(recipe -> recipe.getMealType() == MealType.lunch)
                        .map(recipe -> stringUtil.mapToShortRecipe(recipe.getRecipeId()))
                        .collect(Collectors.toList()) : new ArrayList<>();

        List<Object> dinner = customMealPlan.getCustomMealPlanRecipes() != null ?
                customMealPlan.getCustomMealPlanRecipes().stream()
                        .filter(recipe -> recipe.getMealType() == MealType.dinner)
                        .map(recipe -> stringUtil.mapToShortRecipe(recipe.getRecipeId()))
                        .collect(Collectors.toList()) : new ArrayList<>();

        List<Object> morningSnack = customMealPlan.getCustomMealPlanRecipes() != null ?
                customMealPlan.getCustomMealPlanRecipes().stream()
                        .filter(recipe -> recipe.getMealType() == MealType.morningSnack)
                        .map(recipe -> stringUtil.mapToShortRecipe(recipe.getRecipeId()))
                        .collect(Collectors.toList()) : new ArrayList<>();

        List<Object> afternoonSnack = customMealPlan.getCustomMealPlanRecipes() != null ?
                customMealPlan.getCustomMealPlanRecipes().stream()
                        .filter(recipe -> recipe.getMealType() == MealType.afternoonSnack)
                        .map(recipe -> stringUtil.mapToShortRecipe(recipe.getRecipeId()))
                        .collect(Collectors.toList()) : new ArrayList<>();

        int mealCount = 0;

        if (breakfast != null && !breakfast.isEmpty()) {
            mealCount++;
        }

        if (lunch != null && !lunch.isEmpty()) {
            mealCount++;
        }

        if (dinner != null && !dinner.isEmpty()) {
            mealCount++;
        }

        if (morningSnack != null && !morningSnack.isEmpty()) {
            mealCount++;
        }

        if (afternoonSnack != null && !afternoonSnack.isEmpty()) {
            mealCount++;
        }

        LocalDate date = customMealPlan.getDate();

        Integer dailyCalories = customMealPlan.getDailyCalories();


        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;


        // Calculate total calories, protein, and fat
        for (CustomMealPlanRecipes recipe : customMealPlan.getCustomMealPlanRecipes()) {
            FoodRecipe foodRecipe = foodRecipeRepository.findById(recipe.getRecipeId()).get();
            totalCalories += foodRecipe.getCalories();
            totalProtein += foodRecipe.getProteinContent();
            totalFat += foodRecipe.getFatContent();
        }

        for (CustomMealPlanRecipes recipe : customMealPlan.getCustomMealPlanRecipes()) {
            FoodRecipe foodRecipe = foodRecipeRepository.findById(recipe.getRecipeId()).get();
            totalCalories += foodRecipe.getCalories();
            totalProtein += foodRecipe.getProteinContent();
            totalFat += foodRecipe.getFatContent();
        }

        // Calculate percentages
        Integer totalCaloriesPercentage = totalCalories != 0 ? (int) ((totalCalories / customMealPlan.getDailyCalories()) * 100) : 0;
        Integer totalProteinPercentage = totalCalories != 0 ? (int) ((totalProtein * 4 / totalCalories) * 100) : 0; // Protein has 4 calories per gram
        Integer totalFatPercentage = totalCalories != 0 ? (int) ((totalFat * 9 / totalCalories) * 100) : 0; // Fat has 9 calories per gram

        String description = customMealPlan.getDescription();

        return CustomMealPlanDto.builder()
                .breakfast(breakfast)
                .lunch(lunch)
                .dinner(dinner)
                .morningSnack(morningSnack)
                .afternoonSnack(afternoonSnack)
                .mealCount(mealCount)
                .date(date)
                .dailyCalories(dailyCalories)
                .totalCalories((int) totalCalories)
                .totalCaloriesPercentage(totalCaloriesPercentage)
                .totalProteinPercentage(totalProteinPercentage)
                .totalFatPercentage(totalFatPercentage)
                .description(description)
                .build();
    }

    public void updateMealCount(MealPlan mealPlan) {
        int mealCount = 0;

        if (mealPlan.getBreakfast() != null) {
            mealCount++;
        }
        if (mealPlan.getAfternoonSnack() != null) {
            mealCount++;
        }
        if (mealPlan.getMorningSnack() != null) {
            mealCount++;
        }
        if (mealPlan.getLunch() != null) {
            mealCount++;
        }
        if (mealPlan.getDinner() != null) {
            mealCount++;
        }


        mealPlan.setMealCount(mealCount);

    }


    @Override
    @Transactional
    public void deleteUserMealPlans(User user) {
        List<RecommendMealPlan> userMealPlans = recommendMealPlanRepository.findByUser(user);

        if (!userMealPlans.isEmpty()) {
            for (RecommendMealPlan mealPlan : userMealPlans) {
                recommendMealPlanRecipeRepository.deleteByRecommendMealPlan(mealPlan);
            }

            recommendMealPlanRepository.deleteAll(userMealPlans);
        }
    }

}
