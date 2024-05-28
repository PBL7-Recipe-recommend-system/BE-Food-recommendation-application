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
import java.util.*;

import static java.lang.Math.round;

@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService {
    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;
    private final FoodRecipeRepository foodRecipeRepository;
    private final StringUtil stringUtil;

    @Override
    public MealPlanInput addMealPlans(MealPlanInput mealPlanInput, int userId) {

        User user = checkUser(userId).get();

        MealPlan mealPlan = new MealPlan();
        mealPlan.setUser(user);
        mealPlan.setDate(LocalDate.now());
        mealPlan.setDailyCalories(mealPlanInput.getDailyCalories());
        mealPlan.setTotalCalories(mealPlanInput.getTotalCalories());
        mealPlan.setDescription(mealPlanInput.getDescription());
        mealPlan.setMealCount(mealPlanInput.getMealCount());
        mealPlanRepository.save(mealPlan);
        return mealPlanInput;
    }

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

    @Override
    public List<MealPlanDto> editMealPlans(List<MealPlanInput> mealPlansDtos, int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("The given userId must be greater than zero.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Set<LocalDate> processedDates = new HashSet<>();
        List<MealPlan> mealPlans = new ArrayList<>();
        List<MealPlanDto> mealPlanDtos = new ArrayList<>();

        List<MealPlan> tempMealPlans = (List<MealPlan>) mealPlanRepository.findAllByUser(user);
        int dailyCalories = 0;

        if (!tempMealPlans.isEmpty()) {
            dailyCalories = tempMealPlans.get(0).getDailyCalories();
        }
        for (MealPlanInput mealPlanInput : mealPlansDtos) {
            // Check for duplicate date
            if (!processedDates.add(mealPlanInput.getDate())) {
                // Handle the duplicate date scenario, e.g., skip or throw an exception
                throw new IllegalArgumentException("Duplicate date found: " + mealPlanInput.getDate());
            }

            MealPlan mealPlan = mealPlanRepository.findByUserAndDate(user, mealPlanInput.getDate());
            if (mealPlan == null) {
                mealPlan = new MealPlan();
                mealPlan.setUser(user);
                mealPlan.setDate(mealPlanInput.getDate());
            }

            if (mealPlanInput.getDailyCalories() != null) {
                mealPlanInput.setDailyCalories(mealPlanInput.getDailyCalories());
            } else mealPlanInput.setDailyCalories(dailyCalories);

            updateMealPlanFields(mealPlan, mealPlanInput);

            mealPlans.add(mealPlan);

        }

        mealPlanRepository.saveAll(mealPlans);

        List<MealPlan> output = mealPlanRepository.findAllByUser(user);
        for (MealPlan mealPlan : output) {
            MealPlanDto mealPlanDto = getMealPlanDto(mealPlan);
            mealPlanDtos.add(mealPlanDto);
        }

        return mealPlanDtos;
    }


    private void updateMealPlanFields(MealPlan mealPlan, MealPlanInput input) {
        if (input.getBreakfast() != 0) {
            mealPlan.setBreakfast(getRecipe(input.getBreakfast()));
        }
        if (input.getLunch() != 0) {
            mealPlan.setLunch(getRecipe(input.getLunch()));
        }
        if (input.getDinner() != 0) {
            mealPlan.setDinner(getRecipe(input.getDinner()));
        }
        if (input.getMorningSnack() != 0) {
            mealPlan.setMorningSnack(getRecipe(input.getMorningSnack()));
        }
        if (input.getAfternoonSnack() != 0) {
            mealPlan.setAfternoonSnack(getRecipe(input.getAfternoonSnack()));
        }
        if (input.getDate() != null) {
            mealPlan.setDate(input.getDate());
        }
        if (input.getDescription() != null) {
            mealPlan.setDescription(input.getDescription());
        }

        if (input.getMealCount() != 0) {
            mealPlan.setMealCount(input.getMealCount());
        }
        mealPlan.setTotalCalories(getMealPlanDto(mealPlan).getTotalCalories());
        mealPlan.setDailyCalories(input.getDailyCalories());
    }

    private FoodRecipe getRecipe(Integer recipeId) {
        if (recipeId != null) {
            return foodRecipeRepository.findById(recipeId).orElse(null);
        }
        return null;
    }

    public Object mapToShortRecipe(Integer id) {
        if (foodRecipeRepository.findById(id).isEmpty()) {
            return new ArrayList<>();
        }
        FoodRecipe foodRecipe = foodRecipeRepository.findById(id).get();
        return ShortRecipe.builder()
                .recipeId(foodRecipe.getRecipeId())
                .image(stringUtil.splitStringToList(foodRecipe.getImages()).get(0))
                .totalTime(stringUtil.cleanTime(foodRecipe.getTotalTime()))
                .calories(round(foodRecipe.getCalories()))
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

    public MealPlanDto mapToDto(MealPlan mealPlan) {

        return MealPlanDto.builder()
                .breakfast(mapToShortRecipe(mealPlan.getBreakfast().getRecipeId()))
                .lunch(mapToShortRecipe(mealPlan.getLunch().getRecipeId()))
                .dinner(mapToShortRecipe(mealPlan.getDinner().getRecipeId()))
                .morningSnack(mapToShortRecipe(mealPlan.getMorningSnack().getRecipeId()))
                .afternoonSnack(mapToShortRecipe(mealPlan.getAfternoonSnack().getRecipeId()))
                .date(mealPlan.getDate())
                .description(mealPlan.getDescription())
                .dailyCalories(mealPlan.getDailyCalories())
                .totalCalories(mealPlan.getTotalCalories())
                .mealCount(mealPlan.getMealCount())
                .build();
    }

    @Override
    public List<MealPlanDto> getCurrentMealPlans(Integer userId) {
        if (userRepository.findById(userId).isPresent()) {
            User user = userRepository.findById(userId).get();
            if (!user.isCustomPlan()) {
                throw new RecordNotFoundException("User hasn't created meal plan ");
            }
        }
        List<MealPlan> mealPlans = mealPlanRepository.findCurrentMealPlans(userId, LocalDate.now());
        List<MealPlanDto> output = new ArrayList<>();
        for (MealPlan mealPlan : mealPlans) {
            MealPlanDto mealPlanDto = getMealPlanDto(mealPlan);
            output.add(mealPlanDto);
        }
        return output;
    }

    private MealPlanDto getMealPlanDto(MealPlan mealPlan) {
        MealPlanDto mealPlanDto = new MealPlanDto();
        mealPlanDto.setMealCount(mealPlan.getMealCount());
        mealPlanDto.setDate(mealPlan.getDate());
        mealPlanDto.setDescription(mealPlan.getDescription());

        List<FoodRecipe> allRecipes = new ArrayList<>();
        if (mealPlan.getBreakfast() != null) {
            allRecipes.add(mealPlan.getBreakfast());
        }
        if (mealPlan.getLunch() != null) {
            allRecipes.add(mealPlan.getLunch());
        }
        if (mealPlan.getDinner() != null) {
            allRecipes.add(mealPlan.getDinner());
        }
        if (mealPlan.getAfternoonSnack() != null) {
            allRecipes.add(mealPlan.getAfternoonSnack());
        }
        if (mealPlan.getMorningSnack() != null) {
            allRecipes.add(mealPlan.getMorningSnack());
        }

        // Aggregate total calories, fat, saturated fat, and protein
        double totalCalories = 0;
        double totalFatCalories = 0;
        double totalProteinCalories = 0;

        for (FoodRecipe recipe : allRecipes) {
            totalCalories += recipe.getCalories();
            totalFatCalories += recipe.getFatContent() * 9;
            totalProteinCalories += recipe.getProteinContent() * 4;
        }


        if (totalCalories > 0) {

            double fatPercentage = (totalFatCalories / totalCalories) * 100;

            double proteinPercentage = (totalProteinCalories / totalCalories) * 100;

            mealPlanDto.setTotalFatPercentage((int) fatPercentage);

            mealPlanDto.setTotalProteinPercentage((int) proteinPercentage);
        } else {
            mealPlanDto.setTotalFatPercentage(0);
            mealPlanDto.setTotalProteinPercentage(0);
        }

        mealPlanDto.setBreakfast(mealPlan.getBreakfast() != null ? Collections.singletonList(mapToShortRecipe(mealPlan.getBreakfast().getRecipeId())) : new ArrayList<>());
        mealPlanDto.setDinner(mealPlan.getDinner() != null ? Collections.singletonList(mapToShortRecipe(mealPlan.getDinner().getRecipeId())) : new ArrayList<>());
        mealPlanDto.setLunch(mealPlan.getLunch() != null ? Collections.singletonList(mapToShortRecipe(mealPlan.getLunch().getRecipeId())) : new ArrayList<>());
        mealPlanDto.setAfternoonSnack(mealPlan.getAfternoonSnack() != null ? Collections.singletonList(mapToShortRecipe(mealPlan.getAfternoonSnack().getRecipeId())) : new ArrayList<>());
        mealPlanDto.setMorningSnack(mealPlan.getMorningSnack() != null ? Collections.singletonList(mapToShortRecipe(mealPlan.getMorningSnack().getRecipeId())) : new ArrayList<>());
        mealPlanDto.setDailyCalories(mealPlan.getDailyCalories());

        mealPlanDto.setTotalCalories((int) (totalCalories));
        return mealPlanDto;
    }
//    private MealPlanDto getMealPlanDto(MealPlan mealPlan) {
//        MealPlanDto mealPlanDto = new MealPlanDto();
//        mealPlanDto.setMealCount(mealPlan.getMealCount());
//        mealPlanDto.setDate(mealPlan.getDate());
//        mealPlanDto.setDescription(mealPlan.getDescription());
//        mealPlanDto.setBreakfast(mealPlan.getBreakfast() != null ? Collections.singletonList(mapToShortRecipe(mealPlan.getBreakfast().getRecipeId())) : new ArrayList<>());
//        mealPlanDto.setDinner(mealPlan.getDinner() != null ? Collections.singletonList(mapToShortRecipe(mealPlan.getDinner().getRecipeId())) : new ArrayList<>());
//        mealPlanDto.setLunch(mealPlan.getLunch() != null ? Collections.singletonList(mapToShortRecipe(mealPlan.getLunch().getRecipeId())) : new ArrayList<>());
//        mealPlanDto.setAfternoonSnack(mealPlan.getAfternoonSnack() != null ? Collections.singletonList(mapToShortRecipe(mealPlan.getAfternoonSnack().getRecipeId())) : new ArrayList<>());
//        mealPlanDto.setMorningSnack(mealPlan.getMorningSnack() != null ? Collections.singletonList(mapToShortRecipe(mealPlan.getMorningSnack().getRecipeId())) : new ArrayList<>());
//        mealPlanDto.setDailyCalories(mealPlan.getDailyCalories());
//        mealPlanDto.setTotalCalories(mealPlan.getTotalCalories());
//        return mealPlanDto;
//    }
}
