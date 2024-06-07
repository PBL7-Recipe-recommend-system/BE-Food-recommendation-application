package com.example.BEFoodrecommendationapplication.service.MealPlan;

import com.example.BEFoodrecommendationapplication.dto.MealPlanDto;
import com.example.BEFoodrecommendationapplication.dto.MealPlanInput;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.MealPlan;
import com.example.BEFoodrecommendationapplication.entity.RecommendMealPlan;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.*;
import com.example.BEFoodrecommendationapplication.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService {
    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;
    private final FoodRecipeRepository foodRecipeRepository;
    private final StringUtil stringUtil;
    private final RecommendMealPlanRepository recommendMealPlanRepository;
    private final RecommendMealPlanRecipeRepository recommendMealPlanRecipeRepository;

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
        String description = "";
        if (!tempMealPlans.isEmpty()) {
            dailyCalories = tempMealPlans.get(0).getDailyCalories();
            description = tempMealPlans.get(0).getDescription();
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

                mealPlan.setDailyCalories(mealPlanInput.getDailyCalories());

            } else mealPlan.setDailyCalories(dailyCalories);

            if (mealPlanInput.getDescription() != null) {
                mealPlanInput.setDescription(mealPlanInput.getDescription());
            } else mealPlanInput.setDescription(description);

            updateMealPlanFields(mealPlan, mealPlanInput);
            updateMealCount(mealPlan);
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
    public MealPlanDto deleteRecipeInMealPlan(int userId, MealPlanInput input) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        MealPlan mealPlan = mealPlanRepository.findByUserAndDate(user, input.getDate());


        deleteRecipe(mealPlan, input);

        // Save the updated meal plan
        mealPlanRepository.save(mealPlan);

        return getMealPlanDto(mealPlan);
    }

    private void deleteRecipe(MealPlan mealPlan, MealPlanInput input) {
        int count = 0;
        // Check and set Breakfast
        if (input.getBreakfast() > 0 && mealPlan.getBreakfast() != null && input.getBreakfast() == mealPlan.getBreakfast().getRecipeId()) {
            mealPlan.setBreakfast(null);
            count++;
        }

        // Check and set Lunch
        if (input.getLunch() > 0 && mealPlan.getLunch() != null && input.getLunch() == mealPlan.getLunch().getRecipeId()) {
            mealPlan.setLunch(null);
            count++;
        }

        // Check and set Dinner
        if (input.getDinner() > 0 && mealPlan.getDinner() != null && input.getDinner() == mealPlan.getDinner().getRecipeId()) {
            mealPlan.setDinner(null);
            count++;
        }
        if (input.getMorningSnack() > 0 && mealPlan.getMorningSnack() != null && input.getMorningSnack() == mealPlan.getMorningSnack().getRecipeId()) {
            mealPlan.setMorningSnack(null);
            count++;
        }
        if (input.getAfternoonSnack() > 0 && mealPlan.getAfternoonSnack() != null && input.getAfternoonSnack() == mealPlan.getAfternoonSnack().getRecipeId()) {
            mealPlan.setAfternoonSnack(null);
            count++;
        }
        mealPlan.setMealCount(mealPlan.getMealCount() - count);

    }

    private void updateMealPlanFields(MealPlan mealPlan, MealPlanInput input) {

        if (input.getBreakfast() > 0) {
            mealPlan.setBreakfast(getRecipe(input.getBreakfast()));

        }
        if (input.getLunch() > 0) {
            mealPlan.setLunch(getRecipe(input.getLunch()));

        }
        if (input.getDinner() > 0) {
            mealPlan.setDinner(getRecipe(input.getDinner()));

        }
        if (input.getMorningSnack() > 0) {
            mealPlan.setMorningSnack(getRecipe(input.getMorningSnack()));

        }
        if (input.getAfternoonSnack() > 0) {
            mealPlan.setAfternoonSnack(getRecipe(input.getAfternoonSnack()));

        }
        if (input.getDate() != null) {
            mealPlan.setDate(input.getDate());
        }
        if (input.getDescription() != null) {
            mealPlan.setDescription(input.getDescription());
        }


        mealPlan.setTotalCalories(getMealPlanDto(mealPlan).getTotalCalories());

    }

    private FoodRecipe getRecipe(Integer recipeId) {
        if (recipeId != null) {
            return foodRecipeRepository.findById(recipeId).orElse(null);
        }
        return null;
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
        updateMealCount(mealPlan);
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
            totalFatCalories += recipe.getFatContent() * 9;   // 9 calories per gram of fat
            totalProteinCalories += recipe.getProteinContent() * 4;  // 4 calories per gram of protein

        }

        if (totalCalories > 0) {
            double fatPercentage = (totalFatCalories / totalCalories) * 100;
            double proteinPercentage = (totalProteinCalories / totalCalories) * 100;

            if (mealPlan.getDailyCalories() > 0) {
                double caloriesPercentage = (totalCalories / mealPlan.getDailyCalories()) * 100;
                mealPlanDto.setTotalCaloriesPercentage((int) caloriesPercentage);
                mealPlanDto.setDailyCalories(mealPlan.getDailyCalories());
            } else {
                mealPlanDto.setTotalCaloriesPercentage(0);
                mealPlanDto.setDailyCalories(0);
            }

            mealPlanDto.setTotalFatPercentage((int) fatPercentage);
            mealPlanDto.setTotalProteinPercentage((int) proteinPercentage);

        } else {
            mealPlanDto.setTotalFatPercentage(0);
            mealPlanDto.setTotalProteinPercentage(0);
            mealPlanDto.setTotalCaloriesPercentage(0);
        }


        mealPlanDto.setBreakfast(mealPlan.getBreakfast() != null ? Collections.singletonList(stringUtil.mapToShortRecipe(mealPlan.getBreakfast().getRecipeId())) : new ArrayList<>());
        mealPlanDto.setDinner(mealPlan.getDinner() != null ? Collections.singletonList(stringUtil.mapToShortRecipe(mealPlan.getDinner().getRecipeId())) : new ArrayList<>());
        mealPlanDto.setLunch(mealPlan.getLunch() != null ? Collections.singletonList(stringUtil.mapToShortRecipe(mealPlan.getLunch().getRecipeId())) : new ArrayList<>());
        mealPlanDto.setAfternoonSnack(mealPlan.getAfternoonSnack() != null ? Collections.singletonList(stringUtil.mapToShortRecipe(mealPlan.getAfternoonSnack().getRecipeId())) : new ArrayList<>());
        mealPlanDto.setMorningSnack(mealPlan.getMorningSnack() != null ? Collections.singletonList(stringUtil.mapToShortRecipe(mealPlan.getMorningSnack().getRecipeId())) : new ArrayList<>());

        mealPlanDto.setTotalCalories((int) (totalCalories));
        return mealPlanDto;
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
