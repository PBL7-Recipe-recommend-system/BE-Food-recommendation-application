package com.example.BEFoodrecommendationapplication.service.FoodRecipe;

import com.example.BEFoodrecommendationapplication.dto.RecipeDto;
import com.example.BEFoodrecommendationapplication.dto.SearchResult;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.repository.FoodRecipeRepository;
import com.example.BEFoodrecommendationapplication.util.FoodRecipeSpecification;
import com.example.BEFoodrecommendationapplication.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodRecipeServiceImpl implements FoodRecipeService {

    private final FoodRecipeRepository foodRecipeRepository;
    private final StringUtil stringUtil;


    @Override
    @Cacheable("searchRecipes")
    public Page<SearchResult> search(String name, String category, Integer rating, Integer dateRating, Pageable pageable) {
        Specification<FoodRecipe> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and(FoodRecipeSpecification.nameStartsWith(name));
        }

        if (category != null) {
            spec = spec.and(FoodRecipeSpecification.categoryContains(category));
        }
        if (rating != null) {
            spec = spec.and(FoodRecipeSpecification.ratingIs(rating));
        }


        if (dateRating != null) {
            if (dateRating == 2) {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("datePublished").descending());
            } else if (dateRating == 3) {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("datePublished").ascending());
            }
            else if (dateRating == 4) {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("aggregatedRatings").descending().and(Sort.by("reviewCount").descending()));
            }
        }

        Page<FoodRecipe> foodRecipes = foodRecipeRepository.findAll(spec, pageable);
        if (foodRecipes.isEmpty() && name != null) {
            spec = Specification.where(FoodRecipeSpecification.keywordStartsWith(name));
            foodRecipes = foodRecipeRepository.findAll(spec, pageable);
        }

        return foodRecipes.map(this::mapToSearchResult);
    }

    @Override
    public FoodRecipe findById(Integer id) {
        return foodRecipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodRecipe not found with id " + id));
    }
    @Override
    public Page<SearchResult> findPopularRecipes(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return foodRecipeRepository.findPopularRecipes(pageRequest).map(this::mapToSearchResult);
    }

    @Override
    public RecipeDto mapToDto(FoodRecipe foodRecipe) {
        return RecipeDto.builder()
                .recipeId(foodRecipe.getRecipeId())
                .name(foodRecipe.getName())
                .authorId(foodRecipe.getAuthor().getId())
                .authorName(foodRecipe.getAuthorName())
                .cookTime(cleanTime(foodRecipe.getCookTime()))
                .prepTime(cleanTime(foodRecipe.getPrepTime()))
                .totalTime(cleanTime(foodRecipe.getTotalTime()))
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

    @Override
    public SearchResult mapToSearchResult(FoodRecipe foodRecipe) {
        SearchResult searchResult = new SearchResult();
        searchResult.setId(foodRecipe.getRecipeId());
        searchResult.setName(foodRecipe.getName());
        searchResult.setRating(foodRecipe.getAggregatedRatings());
        searchResult.setAuthorName(foodRecipe.getAuthorName());
        List<String> images = stringUtil.splitStringToList(foodRecipe.getImages());
        if (!images.isEmpty()) {
            searchResult.setImages(images.get(0));
        }
        searchResult.setCalories(foodRecipe.getCalories());
        searchResult.setTotalTime(cleanTime(foodRecipe.getTotalTime()));
        return searchResult;
    }

    public String cleanTime(String time) {
        if (time.startsWith("PT")) {
            return time.replaceFirst("PT", "");
        }
        throw new IllegalArgumentException("Invalid time format");
    }
}