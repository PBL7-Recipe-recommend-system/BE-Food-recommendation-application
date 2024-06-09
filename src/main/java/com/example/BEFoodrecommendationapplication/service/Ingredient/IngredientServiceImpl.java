package com.example.BEFoodrecommendationapplication.service.Ingredient;

import com.example.BEFoodrecommendationapplication.dto.IngredientDto;
import com.example.BEFoodrecommendationapplication.dto.UpdateIngredientsRequest;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.FoodRecipeRepository;
import com.example.BEFoodrecommendationapplication.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final FoodRecipeRepository foodRecipeRepository;
    private final StringUtil stringUtil;


    @Override
    public List<IngredientDto> getRecipeIngredientsById(Integer id) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + id));

        List<String> ingredients = stringUtil.splitStringToList(foodRecipe.getRecipeIngredientsParts());

        return ingredients.stream()
                .map(part -> new IngredientDto(part, null, null))
                .collect(Collectors.toList());
    }


    @Override
    public UpdateIngredientsRequest updateRecipeIngredientNames(Integer recipeId, UpdateIngredientsRequest request) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + recipeId));

        String ingredientsParts = request.getIngredients().stream()
                .map(ingredient -> "\"" + ingredient.getName() + "\"")
                .collect(Collectors.joining(", ", "c(", ")"));
        System.out.println(ingredientsParts);

        foodRecipe.setRecipeIngredientsParts(ingredientsParts);
        foodRecipeRepository.save(foodRecipe);

        return request;
    }

    @Override
    public List<IngredientDto> addIngredient(Integer recipeId, IngredientDto ingredientDTO) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + recipeId));

        String newIngredientPart = "\"" + ingredientDTO.getName() + "\"";
        String existingIngredientsParts = foodRecipe.getRecipeIngredientsParts();

        // Add new ingredient to the existing ingredients string
        if (existingIngredientsParts == null || existingIngredientsParts.isEmpty()) {
            existingIngredientsParts = "c(" + newIngredientPart + ")";
        } else {
            existingIngredientsParts = existingIngredientsParts.substring(0, existingIngredientsParts.length() - 1)
                    + ", " + newIngredientPart + ")";
        }

        foodRecipe.setRecipeIngredientsParts(existingIngredientsParts);
        foodRecipeRepository.save(foodRecipe);

        // Parse the ingredients string back into a list of IngredientDTO
        String ingredientsStr = existingIngredientsParts.substring(2, existingIngredientsParts.length() - 1); // Remove "c(" and ")"
        List<IngredientDto> ingredientList = Stream.of(ingredientsStr.split(", "))
                .map(s -> s.replace("\"", ""))
                .map(name -> new IngredientDto(name, null, null))
                .collect(Collectors.toList());

        return ingredientList;
    }

}
