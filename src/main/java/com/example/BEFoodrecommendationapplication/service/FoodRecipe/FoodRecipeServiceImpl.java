package com.example.BEFoodrecommendationapplication.service.FoodRecipe;

import com.example.BEFoodrecommendationapplication.dto.IngredientDto;
import com.example.BEFoodrecommendationapplication.dto.RecipeDto;
import com.example.BEFoodrecommendationapplication.dto.SearchResult;
import com.example.BEFoodrecommendationapplication.dto.SetCookedRecipeDto;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.RecentSearch;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.entity.UserCookedRecipe;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.repository.*;
import com.example.BEFoodrecommendationapplication.util.FoodRecipeSpecification;
import com.example.BEFoodrecommendationapplication.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodRecipeServiceImpl implements FoodRecipeService {

    private final FoodRecipeRepository foodRecipeRepository;
    private final StringUtil stringUtil;
    private final UserRepository userRepository;
    private final RecentSearchRepository recentSearchRepository;
    private final SavedRecipeRepository savedRecipeRepository;
    private final UserCookedRecipeRepository userCookedRecipeRepository;


    @Override
    @Cacheable("searchRecipes")
    public Page<SearchResult> search(String name, String category, Integer rating, Integer timeRate, Pageable pageable, Integer userId) {
        Specification<FoodRecipe> spec = Specification.where(null);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        // Filter by name
        if (name != null) {
            spec = spec.and(FoodRecipeSpecification.nameStartsWith(name));
        }

        // Filter by category
        if (category != null) {
            spec = spec.and(FoodRecipeSpecification.categoryContains(category));
        }

        // Filter by rating
        if (rating != null) {
            spec = spec.and(FoodRecipeSpecification.ratingIs(rating));
        }

        // Calculate user-specific nutritional requirements
        float calories = user.caloriesCalculator();
        int dietaryGoal;
        if (user.getDietaryGoal() != null) {
            dietaryGoal = user.getDietaryGoal();
        } else {
            dietaryGoal = 1;
        }
        // Adjust protein and fat percentages based on dietary goal
        float proteinPercentage = dietaryGoal == 1 ? 0.30f : dietaryGoal == 2 ? 0.25f : 0.35f;
        float fatPercentage = dietaryGoal == 1 ? 0.25f : dietaryGoal == 2 ? 0.30f : 0.20f;
        float carbPercentage = 1 - (proteinPercentage + fatPercentage);

        // Calculate macronutrient content in grams
        float proteinContent = calories * proteinPercentage / 4; // 4 kcal per gram of protein
        float fatContent = calories * fatPercentage / 9; // 9 kcal per gram of fat
        float carbContent = calories * carbPercentage / 4; // 4 kcal per gram of carbs
        System.out.println(proteinContent * 0.9f + "  " + proteinContent * 1.1f);
        // Add specifications for nutritional content
//        spec = spec.and(FoodRecipeSpecification.caloriesBetween(calories * 0.9f, calories * 1.1f)); // +/- 10% range
//        spec = spec.and(FoodRecipeSpecification.proteinContentBetween(proteinContent * 0.9f, proteinContent * 1.1f));
//        spec = spec.and(FoodRecipeSpecification.fatContentBetween(fatContent * 0.9f, fatContent * 1.1f));
//        spec = spec.and(FoodRecipeSpecification.carbohydrateContentBetween(carbContent * 0.9f, carbContent * 1.1f));

        // Sort by timeRate
        if (timeRate != null) {
            if (timeRate == 2) {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("datePublished").descending());
            } else if (timeRate == 3) {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("datePublished").ascending());
            } else if (timeRate == 4) {
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("aggregatedRatings").descending().and(Sort.by("reviewCount").descending()));
            }
        }

        // Execute the query
        Page<FoodRecipe> foodRecipes = foodRecipeRepository.findAll(spec, pageable);

        // Fallback to keyword search if no results
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

    public boolean isRecipeSavedByUser(Integer userId, Integer recipeId) {
        return savedRecipeRepository.findByUserIdAndRecipeId(userId, recipeId).isPresent();
    }

    @Override
    public List<String> getRecipeInstructionById(Integer id) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + id));

        List<String> instruction = stringUtil.partitionIntoFourParts(stringUtil.splitInstructions(foodRecipe.getRecipeInstructions()));

        return instruction;
    }

    @Override
    public RecipeDto mapToDto(FoodRecipe foodRecipe, Integer userId) {

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
                .recipeInstructions(stringUtil.partitionIntoFourParts(stringUtil.splitInstructions(foodRecipe.getRecipeInstructions())))
                .isSaved(isRecipeSavedByUser(userId, foodRecipe.getRecipeId()))
                .build();
    }

    @Override
    public SearchResult mapToSearchResult(FoodRecipe foodRecipe) {
        SearchResult searchResult = new SearchResult();
        searchResult.setRecipeId(foodRecipe.getRecipeId());
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

    @Override
    public void saveRecentSearch(Integer userId, FoodRecipe foodRecipe) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        Optional<RecentSearch> optionalRecentSearch = recentSearchRepository.findByUserAndRecipe(user, foodRecipe);

        RecentSearch recentSearch;
        if (optionalRecentSearch.isPresent()) {

            recentSearch = optionalRecentSearch.get();
            recentSearch.setTimestamp(LocalDateTime.now());
        } else {

            recentSearch = new RecentSearch();
            recentSearch.setRecipe(foodRecipe);
            recentSearch.setUser(user);
            recentSearch.setTimestamp(LocalDateTime.now());
        }

        recentSearchRepository.save(recentSearch);
    }

    public String cleanTime(String time) {
        if (time == null) {
            return "";
        }
        if (time.startsWith("PT")) {
            return time.replaceFirst("PT", "");
        }
        throw new IllegalArgumentException("Invalid time format");
    }


    @Override
    public void setRecipeAsCooked(Integer userId, SetCookedRecipeDto input) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));
        FoodRecipe foodRecipe = foodRecipeRepository.findById(input.getRecipeId()).orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + input.getRecipeId()));
        LocalDate cookedDate = LocalDate.now();

        // Check if the recipe was already cooked today
        Optional<UserCookedRecipe> existingCookedRecipeOpt = userCookedRecipeRepository.findByUserAndRecipeAndDate(user, foodRecipe, cookedDate);

        if (existingCookedRecipeOpt.isPresent()) {
            // If the recipe was already cooked today, increase the serving size
            UserCookedRecipe existingCookedRecipe = existingCookedRecipeOpt.get();
            existingCookedRecipe.setServingSize(existingCookedRecipe.getServingSize() + input.getServingSize());
            userCookedRecipeRepository.save(existingCookedRecipe);
        } else {
            // If the recipe was not cooked today, create a new record
            UserCookedRecipe newUserCookedRecipe = new UserCookedRecipe();
            newUserCookedRecipe.setUser(user);
            newUserCookedRecipe.setRecipe(foodRecipe);
            newUserCookedRecipe.setIsCooked(true);
            newUserCookedRecipe.setDate(cookedDate);
            newUserCookedRecipe.setServingSize(input.getServingSize());
            newUserCookedRecipe.setMeal(input.getMeal());
            userCookedRecipeRepository.save(newUserCookedRecipe);
        }
    }

    @Override
    public List<UserCookedRecipe> getCookedRecipesByUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User not found with id " + userId));
        return userCookedRecipeRepository.findByUser(user);
    }
}