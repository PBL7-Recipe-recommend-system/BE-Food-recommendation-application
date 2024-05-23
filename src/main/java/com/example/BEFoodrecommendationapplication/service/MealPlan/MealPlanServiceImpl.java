package com.example.BEFoodrecommendationapplication.service.MealPlan;

import com.example.BEFoodrecommendationapplication.dto.MealPlanDto;
import com.example.BEFoodrecommendationapplication.dto.MealPlanInput;
import com.example.BEFoodrecommendationapplication.dto.RecipeDto;
import com.example.BEFoodrecommendationapplication.dto.ShortRecipe;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.MealPlan;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.FoodRecipeRepository;
import com.example.BEFoodrecommendationapplication.repository.MealPlanRepository;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import com.example.BEFoodrecommendationapplication.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService {
    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;
    private  final FoodRecipeRepository foodRecipeRepository;
    private final StringUtil stringUtil;
    @Override
    public List<MealPlanDto> addMealPlans(List<MealPlanInput> mealPlansDtos, int userId) {
        Optional<User> user = userRepository.findById(userId);
        User temp;
        if(user.isPresent())
        {
            temp = user.get();
            temp.setCustomPlan(true);
            userRepository.save(temp);
        }
        else
        {
            throw new RecordNotFoundException("User not found with id: " + userId);
        }
        List<MealPlan> mealPlans = new ArrayList<>();
        List<MealPlanDto> mealPlanDtos = new ArrayList<>();
        for (MealPlanInput mealPlanInput : mealPlansDtos) {

            MealPlan mealPlan = mealPlanRepository.findByUserAndDate(user.get(), mealPlanInput.getDate());
            if (mealPlan == null) {
                mealPlan = new MealPlan();
                mealPlan.setUser(user.get());
                mealPlan.setDate(mealPlanInput.getDate());
            }
            mealPlan.setBreakfast(foodRecipeRepository.findById(mealPlanInput.getBreakfast()).orElse(null));
            mealPlan.setLunch(foodRecipeRepository.findById(mealPlanInput.getLunch()).orElse(null));
            mealPlan.setDinner(foodRecipeRepository.findById(mealPlanInput.getDinner()).orElse(null));
            mealPlan.setSnack1(foodRecipeRepository.findById(mealPlanInput.getSnack1()).orElse(null));
            mealPlan.setSnack2(foodRecipeRepository.findById(mealPlanInput.getSnack2()).orElse(null));
            mealPlan.setDate(mealPlanInput.getDate());
            mealPlan.setDescription(mealPlanInput.getDescription());
            mealPlan.setDailyCalorie(mealPlanInput.getDailyCalorie());
            mealPlan.setTotalCalorie(mealPlanInput.getTotalCalorie());
            mealPlans.add(mealPlan);
            mealPlanDtos.add(mapToDto(mealPlanInput));
        }
        mealPlanRepository.saveAll(mealPlans);
        return mealPlanDtos;
    }
    public ShortRecipe mapToShortRecipe(Integer id){
        if(foodRecipeRepository.findById(id).isEmpty())
        {
            return null;
        }
        FoodRecipe foodRecipe =  foodRecipeRepository.findById(id).get();
        return ShortRecipe.builder()
                .recipeId(foodRecipe.getRecipeId())
                .image(stringUtil.splitStringToList(foodRecipe.getImages()).get(0))
                .totalTime(stringUtil.cleanTime(foodRecipe.getTotalTime()))
                .calories(Math.round(foodRecipe.getCalories()))
                .name(foodRecipe.getName())
                .build();
    }
    public MealPlanDto mapToDto(MealPlanInput mealPlan) {

        return MealPlanDto.builder()
                .breakfast(mapToShortRecipe(mealPlan.getBreakfast()))
                .lunch(mapToShortRecipe(mealPlan.getLunch()))
                .dinner(mapToShortRecipe(mealPlan.getDinner()))
                .snack1(mapToShortRecipe(mealPlan.getSnack1()))
                .snack2(mapToShortRecipe(mealPlan.getSnack2()))
                .date(mealPlan.getDate())
                .description(mealPlan.getDescription())
                .dailyCalorie(mealPlan.getDailyCalorie())
                .totalCalorie(mealPlan.getTotalCalorie())
                .build();
    }
}