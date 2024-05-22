package com.example.BEFoodrecommendationapplication.service.User;

import com.cloudinary.Cloudinary;
import com.example.BEFoodrecommendationapplication.dto.UserDto;
import com.example.BEFoodrecommendationapplication.dto.UserInput;
import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.entity.UserExcludeIngredient;
import com.example.BEFoodrecommendationapplication.entity.UserIncludeIngredient;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.IngredientRepository;
import com.example.BEFoodrecommendationapplication.repository.UserExcludeIngredientRepository;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final IngredientRepository ingredientRepository;
    private final UserExcludeIngredientRepository userExcludeIngredientRepo;

    private final Cloudinary cloudinary;

    public User save(Integer id, UserInput userInput) {
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setWeight(userInput.getWeight());
            user.setHeight(userInput.getHeight());
            user.setGender(userInput.getGender());
            user.setBirthday(userInput.getBirthday());
            user.setDailyActivities(userInput.getDailyActivities());
            user.setMeals(userInput.getMeals());
            user.setDietaryGoal(userInput.getDietaryGoal());

            List<Ingredient> ingredients = ingredientRepository.findByNameIn(userInput.getIngredients());
            Set<UserExcludeIngredient> userExcludeIngredients = new HashSet<>();

            for (Ingredient ingredient : ingredients) {
                UserExcludeIngredient userExcludeIngredient = new UserExcludeIngredient();
                userExcludeIngredient.setUser(user);
                userExcludeIngredient.setIngredient(ingredient);
                userExcludeIngredients.add(userExcludeIngredient);
            }

            if(userExcludeIngredients.isEmpty()) {
                user.setExcludeIngredients(null);
                userExcludeIngredientRepo.saveAll(userExcludeIngredients);
            } else {
                user.setExcludeIngredients(userExcludeIngredients);
                userExcludeIngredientRepo.saveAll(userExcludeIngredients);
            }

            return userRepository.save(user);
        } else {
            throw new RecordNotFoundException("User not found with id : " + id);
        }
    }
    public UserDto getUser(Integer id) {

        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty())
        {
            throw new RecordNotFoundException("User not found with id : " + id);
        }
        return mapUserToUserDto(user.get());

    }

    public UserDto mapUserToUserDto(User user) {

        float[] dietaryRate = {0.8f,1.2f, 1.0f };
        float rate = 1;
        float height = 0;
        float weight = 0;
        String gender = "";
        String dailyActivities = "";
        int meals = 0;
        int dietaryGoal = 0;
        if(user.getHeight() != null)
        {
            height = user.getHeight();
        }
        if(user.getGender() != null)
        {
            gender = user.getGender();
        }
        if(user.getWeight() != null)
        {
            weight = user.getWeight();
        }
        if(user.getDailyActivities() != null)
        {
            dailyActivities = user.getDailyActivities();
        }
        if(user.getMeals() != null)
        {
            meals = user.getMeals();
        }
        if(user.getDietaryGoal() != null)
        {
            rate = dietaryRate[user.getDietaryGoal()];
            dietaryGoal = user.getDietaryGoal();
        }
        return UserDto.builder()
                .name(user.getName())
                .weight(weight)
                .height(height)
                .gender(gender)
                .age(user.calculateAge())
                .dailyActivities(dailyActivities)
                .meals(meals)
                .dietaryGoal(dietaryGoal)
                .bmi(user.calculateBmi())
                .recommendCalories(Math.round(user.caloriesCalculator()*rate))
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
}
