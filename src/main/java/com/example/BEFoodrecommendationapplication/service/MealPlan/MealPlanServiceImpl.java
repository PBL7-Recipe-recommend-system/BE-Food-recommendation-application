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
            if(!user.get().isCustomPlan())
            {
                temp = user.get();
                temp.setCustomPlan(true);
                userRepository.save(temp);
            }

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
            mealPlan.setDailyCalories(mealPlanInput.getDailyCalories());
            mealPlan.setTotalCalories(mealPlanInput.getTotalCalories());
            mealPlan.setMealCount(mealPlanInput.getMealCount());
            mealPlans.add(mealPlan);
            mealPlanDtos.add(mapToDto(mealPlanInput));
        }
        mealPlanRepository.saveAll(mealPlans);
        return mealPlanDtos;
    }
    public Object mapToShortRecipe(Integer id){
        if(foodRecipeRepository.findById(id).isEmpty())
        {
            return new ArrayList<>();
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
                .dailyCalories(mealPlan.getDailyCalories())
                .totalCalories(mealPlan.getTotalCalories())
                .mealCount(mealPlan.getMealCount())
                .build();
    }
    @Override
    public List<MealPlanDto> getCurrentMealPlans(Integer userId) {
        List<MealPlan> mealPlans = mealPlanRepository.findCurrentMealPlans(userId, LocalDate.now());
        List<MealPlanDto> output = new ArrayList<>();
        for (MealPlan mealPlan : mealPlans) {
            MealPlanDto mealPlanDto = new MealPlanDto();
            mealPlanDto.setMealCount(mealPlan.getMealCount());
            mealPlanDto.setDate(mealPlan.getDate());
            mealPlanDto.setDescription(mealPlan.getDescription());
            mealPlanDto.setBreakfast(mealPlan.getBreakfast() != null ? mapToShortRecipe(mealPlan.getBreakfast().getRecipeId()) : new ArrayList<>());
            mealPlanDto.setDinner(mealPlan.getDinner() != null ? mapToShortRecipe(mealPlan.getDinner().getRecipeId()) : new ArrayList<>());
            mealPlanDto.setLunch(mealPlan.getLunch() != null ? mapToShortRecipe(mealPlan.getLunch().getRecipeId()) : new ArrayList<>());
            mealPlanDto.setAfternoonSnack(mealPlan.getAfternoonSnack() != null ? mapToShortRecipe(mealPlan.getAfternoonSnack().getRecipeId()) : new ArrayList<>());
            mealPlanDto.setMorningSnack(mealPlan.getMorningSnack() != null ? mapToShortRecipe(mealPlan.getMorningSnack().getRecipeId()) : new ArrayList<>());
            mealPlanDto.setDailyCalories(mealPlan.getDailyCalories());
            mealPlanDto.setTotalCalories(mealPlan.getTotalCalories());
            output.add(mealPlanDto);
        }
        return output;
    }
}
