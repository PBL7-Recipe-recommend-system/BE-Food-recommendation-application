package com.example.BEFoodrecommendationapplication.service.Ingredient;

import com.example.BEFoodrecommendationapplication.dto.IngredientDto;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.FoodRecipeRepository;
import com.example.BEFoodrecommendationapplication.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
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

}
