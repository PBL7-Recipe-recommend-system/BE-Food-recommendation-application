package com.example.BEFoodrecommendationapplication.service.FoodRecipe;

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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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

        List<String> instruction = stringUtil.splitInstructions(foodRecipe.getRecipeInstructions());

        return instruction;
    }

    private String formatInstructions(List<String> instructions) {
        String result = "c(";
        List<String> quotedInstructions = new ArrayList<>();

        for (String instruction : instructions) {
            quotedInstructions.add("\"" + instruction + "\"");
        }

        result += String.join(", ", quotedInstructions);
        result += ")";
        return result;
    }

    @Override
    public List<String> updateRecipeInstructionAtIndex(Integer id, int index, String newInstruction) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + id));

        // Retrieve and split the instructions
        List<String> instructions = stringUtil.splitInstructions(foodRecipe.getRecipeInstructions());

        // Check if index is valid
        if (index - 1 < 0 || index - 1 >= instructions.size()) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }

        // Update the specific instruction
        instructions.set(index - 1, newInstruction);

        // Reassemble the instructions into the original format
        String updatedInstructions = formatInstructions(instructions);
        foodRecipe.setRecipeInstructions(updatedInstructions);

        // Save the updated recipe
        foodRecipeRepository.save(foodRecipe);

        return instructions;
    }

    @Override
    public List<String> addRecipeInstruction(Integer id, String newInstruction) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + id));

        List<String> instructions = stringUtil.splitInstructions(foodRecipe.getRecipeInstructions());

        instructions.add(newInstruction);

        String updatedInstructions = formatInstructions(instructions);
        foodRecipe.setRecipeInstructions(updatedInstructions);

        foodRecipeRepository.save(foodRecipe);

        return instructions;
    }


    @Override
    public List<String> deleteRecipeInstructionAtIndex(Integer id, int index) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + id));

        List<String> instructions = stringUtil.splitInstructions(foodRecipe.getRecipeInstructions());

        if (index - 1 < 0 || index - 1 >= instructions.size()) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }

        instructions.remove(index - 1);

        String updatedInstructions = formatInstructions(instructions);
        foodRecipe.setRecipeInstructions(updatedInstructions);

        foodRecipeRepository.save(foodRecipe);

        return instructions;
    }


    @Override
    public RecipeDto mapToDto(FoodRecipe foodRecipe, Integer userId) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
        String formattedDate = formatter.format(foodRecipe.getDatePublished());
        return RecipeDto.builder()
                .recipeId(foodRecipe.getRecipeId())
                .name(foodRecipe.getName())
                .authorId(foodRecipe.getAuthor().getId())
                .authorName(foodRecipe.getAuthorName())
                .cookTime(cleanTime(foodRecipe.getCookTime()))
                .prepTime(cleanTime(foodRecipe.getPrepTime()))
                .totalTime(cleanTime(foodRecipe.getTotalTime()))
                .datePublished(formattedDate)
                .description(foodRecipe.getDescription())
                .images(stringUtil.splitStringToList(foodRecipe.getImages()))
                .recipeCategory(foodRecipe.getRecipeCategory())
                .keywords(stringUtil.splitStringToList(foodRecipe.getKeywords()))
                .recipeIngredientsQuantities(stringUtil.splitBySlash(foodRecipe.getRecipeIngredientsQuantities()))
                .recipeIngredientsParts(stringUtil.splitBySlash(foodRecipe.getIngredientsRaw()))
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
                .recipeInstructions(stringUtil.splitInstructions(foodRecipe.getRecipeInstructions()))
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
        if (time == null || time.isEmpty() || !time.startsWith("PT")) {
            return time;
        }
        return time.replaceFirst("PT", "");
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

    @Override
    public FoodRecipe addFoodRecipe(String name, Integer authorId) {
        User author = userRepository.findById(authorId).orElseThrow(() -> new RecordNotFoundException("User not found with id " + authorId));
        if (name == null) {
            throw new IllegalArgumentException("Recipe name must not be null");
        }

        FoodRecipe newRecipe = new FoodRecipe();
        newRecipe.setName(name);
        newRecipe.setAuthorName(author.getName());
        newRecipe.setAuthor(author);

        newRecipe.setDatePublished(new Date());  // Set publishing date to now

        // Set default values for other fields
        newRecipe.setCookTime("");
        newRecipe.setPrepTime("");
        newRecipe.setTotalTime("");
        newRecipe.setDescription("");
        newRecipe.setImages("");
        newRecipe.setRecipeCategory("");
        newRecipe.setKeywords("");
        newRecipe.setRecipeIngredientsQuantities("");
        newRecipe.setRecipeIngredientsParts("");
        newRecipe.setAggregatedRatings(0);
        newRecipe.setReviewCount(0);
        newRecipe.setCalories(0.0f);
        newRecipe.setFatContent(0.0f);
        newRecipe.setSaturatedFatContent(0.0f);
        newRecipe.setCholesterolContent(0.0f);
        newRecipe.setSodiumContent(0.0f);
        newRecipe.setCarbonhydrateContent(0.0f);
        newRecipe.setFiberContent(0.0f);
        newRecipe.setSugarContent(0.0f);
        newRecipe.setProteinContent(0.0f);
        newRecipe.setRecipeServings(0);
        newRecipe.setRecipeInstructions("");
        newRecipe.setIngredientsRaw("");

        // Save the new recipe to the repository
        foodRecipeRepository.save(newRecipe);

        return newRecipe;
    }

    @Override
    public FoodRecipe updateFoodRecipe(Integer recipeId, RecipeDto recipeDto) {
        FoodRecipe foodRecipe = foodRecipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecordNotFoundException("Recipe not found with id " + recipeId));
        String images = recipeDto.getImages() != null ?
                "c(" + recipeDto.getImages().stream()
                        .map(image -> "\"" + image + "\"")
                        .collect(Collectors.joining(",")) + ")"
                : "";
        foodRecipe.setImages(images);

        String keywords = recipeDto.getKeywords() != null ?
                "c(" + recipeDto.getKeywords().stream()
                        .map(keyword -> "\"" + keyword + "\"")
                        .collect(Collectors.joining(",")) + ")"
                : "";
        foodRecipe.setKeywords(keywords);
        // Use a ternary operator to check for null and set the previous value if so
        foodRecipe.setCookTime(recipeDto.getCookTime() != null ? recipeDto.getCookTime() : foodRecipe.getCookTime());
        foodRecipe.setPrepTime(recipeDto.getPrepTime() != null ? recipeDto.getPrepTime() : foodRecipe.getPrepTime());
        foodRecipe.setTotalTime(recipeDto.getTotalTime() != null ? recipeDto.getTotalTime() : foodRecipe.getTotalTime());
        foodRecipe.setDescription(recipeDto.getDescription() != null ? recipeDto.getDescription() : foodRecipe.getDescription());
        foodRecipe.setRecipeCategory(recipeDto.getRecipeCategory() != null ? recipeDto.getRecipeCategory() : foodRecipe.getRecipeCategory());
        foodRecipe.setAggregatedRatings(recipeDto.getAggregatedRatings() != null ? recipeDto.getAggregatedRatings() : foodRecipe.getAggregatedRatings());
        foodRecipe.setReviewCount(recipeDto.getReviewCount() != null ? recipeDto.getReviewCount() : foodRecipe.getReviewCount());
        foodRecipe.setCalories(recipeDto.getCalories() != null ? recipeDto.getCalories() : foodRecipe.getCalories());
        foodRecipe.setFatContent(recipeDto.getFatContent() != null ? recipeDto.getFatContent() : foodRecipe.getFatContent());
        foodRecipe.setSaturatedFatContent(recipeDto.getSaturatedFatContent() != null ? recipeDto.getSaturatedFatContent() : foodRecipe.getSaturatedFatContent());
        foodRecipe.setCholesterolContent(recipeDto.getCholesterolContent() != null ? recipeDto.getCholesterolContent() : foodRecipe.getCholesterolContent());
        foodRecipe.setSodiumContent(recipeDto.getSodiumContent() != null ? recipeDto.getSodiumContent() : foodRecipe.getSodiumContent());
        foodRecipe.setCarbonhydrateContent(recipeDto.getCarbonhydrateContent() != null ? recipeDto.getCarbonhydrateContent() : foodRecipe.getCarbonhydrateContent());
        foodRecipe.setFiberContent(recipeDto.getFiberContent() != null ? recipeDto.getFiberContent() : foodRecipe.getFiberContent());
        foodRecipe.setSugarContent(recipeDto.getSugarContent() != null ? recipeDto.getSugarContent() : foodRecipe.getSugarContent());
        foodRecipe.setProteinContent(recipeDto.getProteinContent() != null ? recipeDto.getProteinContent() : foodRecipe.getProteinContent());
        foodRecipe.setRecipeServings(recipeDto.getRecipeServings() != null ? recipeDto.getRecipeServings() : foodRecipe.getRecipeServings());

        foodRecipeRepository.save(foodRecipe);
        return foodRecipe;
    }

}