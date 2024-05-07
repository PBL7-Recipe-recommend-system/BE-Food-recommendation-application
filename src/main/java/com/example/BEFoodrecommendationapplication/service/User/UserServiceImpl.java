package com.example.BEFoodrecommendationapplication.service.User;

import com.example.BEFoodrecommendationapplication.dto.UserInput;
import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.IngredientRepository;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final IngredientRepository ingredientRepository;

    public User save(Integer id, UserInput userInput) {
        Optional<User> optionalUser = userRepository.findById(id);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setWeight(userInput.getWeight());
            user.setHeight(userInput.getHeight());
            user.setGender(userInput.getGender());
            user.setBirthday(userInput.getBirthday());
            user.setDailyActivities(userInput.getDaily_activities());
            user.setMeals(userInput.getMeals());
            user.setDietaryGoal(userInput.getDietary_goal());

            List<Ingredient> ingredients = ingredientRepository.findByNameIn(userInput.getIngredients());
            Set<Ingredient> ingredientSet = new HashSet<>(ingredients);
            if(ingredientSet.isEmpty())
            {
                user.setIngredients(null);
            }
            else{
                user.setIngredients(ingredientSet);
            }

            return userRepository.save(user);
        } else {
            throw new RecordNotFoundException("User not found with id : " + id);
        }
    }
    public User getUser(Integer id) {
        try {
            return userRepository.findById(id).orElseThrow();
        }catch(Exception e)
        {
            throw new RecordNotFoundException("User not found with id : " + id);
        }

    }
}
