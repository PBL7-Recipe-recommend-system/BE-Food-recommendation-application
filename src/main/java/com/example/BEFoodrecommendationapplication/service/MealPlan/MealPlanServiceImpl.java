package com.example.BEFoodrecommendationapplication.service.MealPlan;

import com.example.BEFoodrecommendationapplication.dto.MealPlanDto;
import com.example.BEFoodrecommendationapplication.dto.MealPlanInput;
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

import java.time.LocalDate;
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
    public MealPlanInput addMealPlans(MealPlanInput mealPlanInput, int userId) {

        User user = checkUser(userId).get();
        if(user.isCustomPlan())
        {
            return mealPlanInput;
        }
        MealPlan mealPlan = new MealPlan();
        mealPlan.setUser(user);
        mealPlan.setDate(LocalDate.now());
        mealPlan.setDescription(mealPlanInput.getDescription());
        mealPlan.setMealCount(mealPlanInput.getMealCount());
        mealPlanRepository.save(mealPlan);
       return mealPlanInput;
    }

    private Optional<User> checkUser(int userId) {
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
        return user;
    }

    @Override
    public List<MealPlanDto> editMealPlans(List<MealPlanInput> mealPlansDtos, int userId) {
        Optional<User> user = checkUser(userId);
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
            mealPlan.setMorningSnack(foodRecipeRepository.findById(mealPlanInput.getMorningSnack()).orElse(null));
            mealPlan.setAfternoonSnack(foodRecipeRepository.findById(mealPlanInput.getAfternoonSnack()).orElse(null));
            mealPlan.setDate(mealPlanInput.getDate());
            mealPlan.setDescription(mealPlanInput.getDescription());
            mealPlan.setDailyCalorie(mealPlanInput.getDailyCalories());
            mealPlan.setTotalCalorie(mealPlanInput.getTotalCalories());
            mealPlan.setMealCount(mealPlanInput.getMealCount());
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
                .morningSnack(mapToShortRecipe(mealPlan.getMorningSnack()))
                .afternoonSnack(mapToShortRecipe(mealPlan.getAfternoonSnack()))
                .date(mealPlan.getDate())
                .description(mealPlan.getDescription())
                .dailyCalorie(mealPlan.getDailyCalories())
                .totalCalorie(mealPlan.getTotalCalories())
                .build();
    }
}
