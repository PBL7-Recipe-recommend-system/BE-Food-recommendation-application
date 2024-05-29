package com.example.BEFoodrecommendationapplication.service.User;

import com.cloudinary.Cloudinary;
import com.example.BEFoodrecommendationapplication.dto.UserDto;
import com.example.BEFoodrecommendationapplication.dto.UserInput;
import com.example.BEFoodrecommendationapplication.entity.*;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.IngredientRepository;
import com.example.BEFoodrecommendationapplication.repository.SavedRecipeRepository;
import com.example.BEFoodrecommendationapplication.repository.UserExcludeIngredientRepository;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import com.example.BEFoodrecommendationapplication.service.MealPlan.MealPlanService;
import com.example.BEFoodrecommendationapplication.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final UserExcludeIngredientRepository userExcludeIngredientRepo;
    private final Cloudinary cloudinary;
    private final SavedRecipeRepository savedRecipeRepository;
    private final StringUtil stringUtil;
    private final MealPlanService mealPlanService;

    @Override
    public void saveOrDeleteRecipeForUser(User user, FoodRecipe recipe, boolean save) {
        if (save) {
            SavedRecipe savedRecipe = new SavedRecipe();
            savedRecipe.setUser(user);
            savedRecipe.setRecipe(recipe);
            savedRecipeRepository.save(savedRecipe);
        } else {
            SavedRecipe savedRecipe = savedRecipeRepository.findByUserAndRecipe(user, recipe);
            if (savedRecipe != null) {
                savedRecipeRepository.delete(savedRecipe);
            } else {
                throw new RecordNotFoundException("Save recipe not found");
            }
        }
    }

    @Override
    public List<Object> getSavedRecipesByUser(Integer userId) {
        List<SavedRecipe> savedRecipes = savedRecipeRepository.findByUserId(userId);
        List<Object> output = new ArrayList<>();
        for (SavedRecipe savedRecipe : savedRecipes) {
            output.add(mapToDto(savedRecipe));
        }
        return output;
    }

    private Object mapToDto(SavedRecipe savedRecipe) {
        return stringUtil.mapToShortRecipe(savedRecipe.getRecipe().getRecipeId());
    }

    public User save(Integer id, UserInput userInput) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (userInput.getName() != null) {
                user.setName(userInput.getName());
            }
            if (userInput.getWeight() != 0) {
                user.setWeight(userInput.getWeight());
            }
            if (userInput.getHeight() != 0) {
                user.setHeight(userInput.getHeight());
            }
            if (userInput.getGender() != null) {
                user.setGender(userInput.getGender());
            }
            if (userInput.getBirthday() != null) {
                user.setBirthday(userInput.getBirthday());
            }
            if (userInput.getDailyActivities() != null) {
                user.setDailyActivities(userInput.getDailyActivities());
            }
            if (userInput.getMeals() != null) {
                user.setMeals(userInput.getMeals());
            }
            if (userInput.getDietaryGoal() != null) {
                user.setDietaryGoal(userInput.getDietaryGoal());
            }

            List<Ingredient> ingredients = ingredientRepository.findByNameIn(userInput.getIngredients());
            Set<UserExcludeIngredient> userExcludeIngredients = new HashSet<>();

            for (Ingredient ingredient : ingredients) {
                UserExcludeIngredient userExcludeIngredient = userExcludeIngredientRepo.findByUserAndIngredient(user, ingredient);
                if (userExcludeIngredient == null) {
                    userExcludeIngredient = new UserExcludeIngredient();
                    userExcludeIngredient.setUser(user);
                    userExcludeIngredient.setIngredient(ingredient);
                    userExcludeIngredients.add(userExcludeIngredient);
                }
            }

            if (!userExcludeIngredients.isEmpty()) {
                user.setExcludeIngredients(userExcludeIngredients);
                userExcludeIngredientRepo.saveAll(userExcludeIngredients);
            }
            mealPlanService.deleteUserMealPlans(user);
            return userRepository.save(user);
        } else {
            throw new RecordNotFoundException("User not found with id : " + id);
        }
    }


    public UserDto getUser(Integer id) {

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RecordNotFoundException("User not found with id : " + id);
        }
        return mapUserToUserDto(user.get());

    }

    public UserDto mapUserToUserDto(User user) {

        float[] dietaryRate = {0.8f, 1.2f, 1.0f};
        float rate = 1;
        float height = 0;
        float weight = 0;
        String gender = "";
        String dailyActivities = "";
        int meals = 0;
        int dietaryGoal = 0;
        if (user.getHeight() != null) {
            height = user.getHeight();
        }
        if (user.getGender() != null) {
            gender = user.getGender();
        }
        if (user.getWeight() != null) {
            weight = user.getWeight();
        }
        if (user.getDailyActivities() != null) {
            dailyActivities = user.getDailyActivities();
        }
        if (user.getMeals() != null) {
            meals = user.getMeals();
        }
        if (user.getDietaryGoal() != null) {
            rate = dietaryRate[user.getDietaryGoal()];
            dietaryGoal = user.getDietaryGoal();
        }
        List<String> includeIngredientNames = user.getIncludeIngredients().stream()
                .map(UserIncludeIngredient::getIngredient)
                .map(Ingredient::getName)
                .toList();

        List<String> excludeIngredientNames = user.getExcludeIngredients().stream()
                .map(UserExcludeIngredient::getIngredient)
                .map(Ingredient::getName)
                .toList();


        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .weight(weight)
                .height(height)
                .gender(gender)
                .age(user.calculateAge())
                .dailyActivities(dailyActivities)
                .meals(meals)
                .dietaryGoal(dietaryGoal)
                .bmi(user.calculateBmi())
                .isCustomPlan(user.isCustomPlan())
                .recommendCalories(Math.round(user.caloriesCalculator() * rate))
                .includeIngredients(includeIngredientNames)
                .excludeIngredients(excludeIngredientNames)
                .build();
    }


    @Override
    public String uploadAvatar(MultipartFile multipartFile, Integer id) throws IOException {
        User user = userRepository.findById(id).orElseThrow(() -> new
                RecordNotFoundException("user not found"));
        String imageUrl = cloudinary.uploader()
                .upload(multipartFile.getBytes(),
                        Map.of("public_id", UUID.randomUUID().toString()))
                .get("url")
                .toString();
        user.setAvatar(imageUrl);
        userRepository.save(user);
        return imageUrl;
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodRecipe not found with id " + id));
    }
}
