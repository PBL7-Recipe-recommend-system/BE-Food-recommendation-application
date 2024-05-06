package com.example.BEFoodrecommendationapplication.service.FoodRecipe;

import com.example.BEFoodrecommendationapplication.dto.RecipeDto;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.Review;
import com.example.BEFoodrecommendationapplication.repository.FoodRecipeRepository;
import com.example.BEFoodrecommendationapplication.repository.ReviewRepository;
import com.example.BEFoodrecommendationapplication.service.AuthenticationService;
import com.example.BEFoodrecommendationapplication.util.FoodRecipeSpecification;
import com.example.BEFoodrecommendationapplication.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodRecipeServiceImpl implements FoodRecipeService {

    private final FoodRecipeRepository foodRecipeRepository;
    private final ReviewRepository reviewRepository;
    private final StringUtil stringUtil;


    @Override
    public Page<RecipeDto> search(String name, String category, Integer rating, Pageable pageable) {
        Specification<FoodRecipe> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and(FoodRecipeSpecification.nameContains(name));
        }

        if (category != null) {
            spec = spec.and(FoodRecipeSpecification.categoryContains(category));
        }
        if (rating != null) {
            spec = spec.and(FoodRecipeSpecification.ratingIs(rating));
        }

        Page<FoodRecipe> foodRecipes = foodRecipeRepository.findAll(spec, pageable);
        if (foodRecipes.isEmpty() && name != null) {
            spec = Specification.where(FoodRecipeSpecification.keywordContains(name));
            foodRecipes = foodRecipeRepository.findAll(spec, pageable);
        }

        return foodRecipes.map(this::mapToDto);
    }

    public List<FoodRecipe> search(String keyword) {

        List<FoodRecipe> foodRecipes = foodRecipeRepository.findAll();

        String lowerCaseKeyword = keyword.toLowerCase();


        return foodRecipes.stream()
                .filter(foodRecipe -> stringUtil.splitStringToList(foodRecipe.getKeywords()).stream()
                        .anyMatch(k -> k.toLowerCase().contains(lowerCaseKeyword)))
                .collect(Collectors.toList());
    }
    @Override
    public FoodRecipe findById(Integer id) {
        return foodRecipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodRecipe not found with id " + id));
    }

    @Override
    public RecipeDto mapToDto(FoodRecipe foodRecipe) {
        return RecipeDto.builder()
                .recipeId(foodRecipe.getRecipeId())
                .name(foodRecipe.getName())
                .authorId(foodRecipe.getAuthor().getId())
                .authorName(foodRecipe.getAuthorName())
                .cookTime(foodRecipe.getCookTime())
                .prepTime(foodRecipe.getPrepTime())
                .totalTime(foodRecipe.getTotalTime())
                .datePublished(foodRecipe.getDatePublished())
                .description(foodRecipe.getDescription())
                .images(stringUtil.splitStringToList(foodRecipe.getImages()))
                .recipeCategory(foodRecipe.getRecipeCategory())
                .keywords(stringUtil.splitStringToList(foodRecipe.getKeywords()))
                .recipeIngredientsQuantities(stringUtil.splitStringToList(foodRecipe.getRecipeIngredientsQuantities()))
                .recipeIngredientsParts(stringUtil.splitStringToList(foodRecipe.getRecipeIngredientsParts()))
                .aggregatedRatings(foodRecipe.getAggregatedRatings())
                .reviewCount(foodRecipe.getReviewCount())
                .calories(foodRecipe.getCalories())
                .fatContent(foodRecipe.getFatContent())
                .saturatedFatContent(foodRecipe.getSaturatedFatContent())
                .cholesterolContent(foodRecipe.getCholesterolContent())
                .sodiumContent(foodRecipe.getSodiumContent())
                .carbonhydrateContent(foodRecipe.getCarbonhydrateContent())
                .fiberContent(foodRecipe.getFiberContent())
                .sugarContent(foodRecipe.getSugarContent())
                .proteinContent(foodRecipe.getProteinContent())
                .recipeServings(foodRecipe.getRecipeServings())
                .recipeYeild(foodRecipe.getRecipeYeild())
                .recipeInstructions(stringUtil.splitStringToList(foodRecipe.getRecipeInstructions()))
                .build();
    }
}