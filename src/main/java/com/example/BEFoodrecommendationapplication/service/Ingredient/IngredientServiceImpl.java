package com.example.BEFoodrecommendationapplication.service.Ingredient;

import com.example.BEFoodrecommendationapplication.dto.IngredientDto;
import com.example.BEFoodrecommendationapplication.dto.UpdateIngredientsRequest;
import com.example.BEFoodrecommendationapplication.entity.*;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.*;
import com.example.BEFoodrecommendationapplication.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final FoodRecipeRepository foodRecipeRepository;
    private final IngredientRepository ingredientRepository;
    private final StringUtil stringUtil;
    private final UserIncludeIngredientRepository userIncludeIngredientRepository;
    private final UserExcludeIngredientRepository userExcludeIngredientRepository;
    private final UserRepository userRepository;


    @Override
    public List<IngredientDto> getRecipeIngredientsById(Integer id) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + id));

        List<String> quantities = foodRecipe.getRecipeIngredientsQuantities() != null
                ? stringUtil.splitBySlash(foodRecipe.getRecipeIngredientsQuantities())
                : Collections.emptyList();
        List<String> units = foodRecipe.getIngredientsRaw() != null
                ? stringUtil.splitBySlash(foodRecipe.getIngredientsRaw())
                : Collections.emptyList();

        int maxSize = Math.max(quantities.size(), units.size());
        List<IngredientDto> ingredients = new ArrayList<>();

        for (int i = 0; i < maxSize; i++) {
            String name = i < units.size() ? units.get(i) : null;
            String quantity = i < quantities.size() ? quantities.get(i) : null;


            ingredients.add(new IngredientDto(name, quantity));
        }

        return ingredients;
    }


    @Override
    public UpdateIngredientsRequest updateRecipeIngredientNamesAndQuantities(Integer recipeId, UpdateIngredientsRequest request) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + recipeId));

        // Joining ingredient names with "///" as the separator
        String ingredientsRaw = request.getIngredients().stream()
                .map(IngredientDto::getName)  // Assuming getName() directly returns the ingredient name
                .collect(Collectors.joining("///"));

        // Joining ingredient quantities with "///" as the separator
        String ingredientsQuantities = request.getIngredients().stream()
                .map(IngredientDto::getQuantity)  // Assuming getQuantity() returns the ingredient quantity
                .map(qty -> qty == null ? "None" : qty)  // Handle null quantities
                .collect(Collectors.joining("///"));

        // Update the ingredientsRaw and ingredientsQuantities fields of the FoodRecipe entity
        foodRecipe.setIngredientsRaw(ingredientsRaw);
        foodRecipe.setRecipeIngredientsQuantities(ingredientsQuantities);
        foodRecipeRepository.save(foodRecipe);

        // Log the updated ingredients string for debugging purposes
        System.out.println("Updated Ingredients: " + ingredientsRaw);
        System.out.println("Updated Quantities: " + ingredientsQuantities);

        return request;
    }


    @Override
    public List<IngredientDto> addIngredient(Integer recipeId, IngredientDto ingredientDTO) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + recipeId));

        // Add ingredient name to ingredients_raw
        String newIngredientName = ingredientDTO.getName();
        String existingIngredientsRaw = foodRecipe.getIngredientsRaw();
        if (existingIngredientsRaw == null || existingIngredientsRaw.isEmpty()) {
            existingIngredientsRaw = newIngredientName;
        } else {
            existingIngredientsRaw += "///" + newIngredientName;
        }
        foodRecipe.setIngredientsRaw(existingIngredientsRaw);

        // Add ingredient quantity to recipe_ingredients_quantities
        String newQuantity = ingredientDTO.getQuantity();
        String existingQuantities = foodRecipe.getRecipeIngredientsQuantities();
        if (existingQuantities == null || existingQuantities.isEmpty()) {
            existingQuantities = newQuantity == null ? "None" : newQuantity;  // Handle null quantity
        } else {
            existingQuantities += "///" + (newQuantity == null ? "None" : newQuantity);
        }
        foodRecipe.setRecipeIngredientsQuantities(existingQuantities);

        // Save the updates
        foodRecipeRepository.save(foodRecipe);

        // Construct the return list based on updated data
        List<String> names = Arrays.asList(existingIngredientsRaw.split("///"));
        List<String> quantities = Arrays.asList(existingQuantities.split("///"));
        List<IngredientDto> ingredientList = IntStream.range(0, names.size())
                .mapToObj(i -> new IngredientDto(names.get(i), quantities.size() > i ? quantities.get(i) : null))
                .collect(Collectors.toList());

        return ingredientList;
    }

    public List<String> getAllIngredient(String name) {
        List<Ingredient> ingredients = ingredientRepository.findAllByNameContainingIgnoreCase(name);
        if (ingredients.isEmpty()) {
            return Collections.emptyList();
        }

        return ingredients.stream()
                .map(Ingredient::getName)
                .map(stringUtil::capitalizeFirstLetterOfEachWord)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserIngredients(Integer userId, String includeOrExclude) {
        if ("include".equalsIgnoreCase(includeOrExclude)) {
            return userIncludeIngredientRepository.findAllByUserId(userId).stream()
                    .map(UserIncludeIngredient::getIngredient)
                    .map(Ingredient::getName)
                    .map(stringUtil::capitalizeFirstLetterOfEachWord)
                    .collect(Collectors.toList());
        } else if ("exclude".equalsIgnoreCase(includeOrExclude)) {
            return userExcludeIngredientRepository.findAllByUserId(userId).stream()
                    .map(UserExcludeIngredient::getIngredient)
                    .map(Ingredient::getName)
                    .map(stringUtil::capitalizeFirstLetterOfEachWord)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Invalid includeOrExclude parameter. Must be 'include' or 'exclude'.");
        }
    }

    @Override
    public void addUserIngredients(Integer userId, List<String> ingredientNames, String includeOrExclude) {
        for (String ingredientName : ingredientNames) {
            // Find the ingredient by name, ignoring case
            Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(ingredientName)
                    .orElseThrow(() -> new RecordNotFoundException("Ingredient not found with name " + ingredientName));
            User user = userRepository.findById(userId).get();
            if ("include".equalsIgnoreCase(includeOrExclude)) {
                // Create a new UserIncludeIngredient and save it
                UserIncludeIngredient userIncludeIngredient = new UserIncludeIngredient();
                userIncludeIngredient.setUser(user);
                userIncludeIngredient.setIngredient(ingredient);
                userIncludeIngredientRepository.save(userIncludeIngredient);
            } else if ("exclude".equalsIgnoreCase(includeOrExclude)) {
                // Create a new UserExcludeIngredient and save it
                UserExcludeIngredient userExcludeIngredient = new UserExcludeIngredient();
                userExcludeIngredient.setUser(user);
                userExcludeIngredient.setIngredient(ingredient);
                userExcludeIngredientRepository.save(userExcludeIngredient);
            } else {
                throw new IllegalArgumentException("Invalid includeOrExclude parameter. Must be 'include' or 'exclude'.");
            }
        }
    }

    @Override
    @Transactional
    public void deleteUserIngredient(Integer userId, String ingredientName, String includeOrExclude) {
        // Find the ingredient by name, ignoring case
        Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(ingredientName)
                .orElseThrow(() -> new RecordNotFoundException("Ingredient not found with name " + ingredientName));
        User user = userRepository.findById(userId).get();
        if ("include".equalsIgnoreCase(includeOrExclude)) {
            // Find the UserIncludeIngredient and delete it
            UserIncludeIngredient userIncludeIngredient = userIncludeIngredientRepository.findByUserAndIngredient(user, ingredient)
                    .orElseThrow(() -> new RecordNotFoundException("UserIncludeIngredient not found for user " + userId + " and ingredient " + ingredientName));
            userIncludeIngredientRepository.delete(userIncludeIngredient);
        } else if ("exclude".equalsIgnoreCase(includeOrExclude)) {
            // Find the UserExcludeIngredient and delete it
            UserExcludeIngredient userExcludeIngredient = userExcludeIngredientRepository.findByUserAndIngredient(user, ingredient)
                    .orElseThrow(() -> new RecordNotFoundException("UserExcludeIngredient not found for user " + userId + " and ingredient " + ingredientName));
            userExcludeIngredientRepository.delete(userExcludeIngredient);
        } else {
            throw new IllegalArgumentException("Invalid includeOrExclude parameter. Must be 'include' or 'exclude'.");
        }
    }
}
