package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.dto.SearchResult;
import com.example.BEFoodrecommendationapplication.dto.SetCookedRecipeDto;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.RecentSearch;
import com.example.BEFoodrecommendationapplication.repository.FoodRecipeRepository;
import com.example.BEFoodrecommendationapplication.repository.RecentSearchRepository;
import com.example.BEFoodrecommendationapplication.service.FoodRecipe.FoodRecipeService;
import com.example.BEFoodrecommendationapplication.util.AuthenticationUtils;
import com.example.BEFoodrecommendationapplication.util.ResponseBuilderUtil;
import com.example.BEFoodrecommendationapplication.util.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
@CrossOrigin("${allowed.origins}")
@Tag(name = "Food Recipe")
public class FoodRecipeController {
    private final FoodRecipeService foodRecipeService;

    private final FoodRecipeRepository foodRecipeRepository;

    private final RecentSearchRepository recentSearchRepository;

    @Operation(summary = "Search Recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search Recipe successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Search Recipe failed")})
    @GetMapping("/search")
    @Cacheable("searchFilter")
    public ResponseEntity<Response> search(@RequestParam(required = false) String name,
                                           @RequestParam(required = false) String category,
                                           @RequestParam(required = false) Integer rating,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(defaultValue = "1") Integer timeRate) {
        try {
            Integer userId = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();
            Page<SearchResult> listRecipes = foodRecipeService.search(name, category, rating, timeRate, PageRequest.of(page - 1, size), userId);

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    listRecipes,
                    "Search Recipe successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }

    }

    @Operation(summary = "Get Detail Recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Detail successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Get Detail failed")})
    @GetMapping("")
    @Cacheable("getDetail")
    public ResponseEntity<Response> getRecipeById(@RequestParam Integer id) {
        try {

            FoodRecipe foodRecipe = foodRecipeService.findById(id);
            Integer userId = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();
            foodRecipeService.saveRecentSearch(userId, foodRecipe);

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    foodRecipeService.mapToDto(foodRecipe, userId),
                    "Get Detail successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }


    @Operation(summary = "Get Recent Search Recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Recent Search successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Get Recent Search failed")})
    @GetMapping("/recent")
    @Cacheable("recentSearch")
    public ResponseEntity<Response> getRecentViews() {
        try {

            List<SearchResult> recentSearches = recentSearchRepository
                    .findTop10ByUser_IdOrderByTimestampDesc(AuthenticationUtils.getUserFromSecurityContext().getId())
                    .stream()
                    .map(RecentSearch::getRecipe)
                    .map(foodRecipeService::mapToSearchResult)
                    .toList();

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    recentSearches,
                    "Get Recent Search successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }

    }

    @Operation(summary = "Get Popular Recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Popular successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Get Popular failed")})
    @GetMapping("/popular")
    @Cacheable("getPopular")
    public ResponseEntity<Response> getPopularRecipes(@RequestParam Integer page,
                                                      @RequestParam Integer size) {
        try {

            Page<SearchResult> popularRecipes = foodRecipeService.findPopularRecipes(page, size);

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    popularRecipes,
                    "Get popular recipes successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {


            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }

    @Operation(summary = "Get Category List")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Category List successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Get Category List failed")})
    @GetMapping("/categories")
    @Cacheable("getCategory")
    public ResponseEntity<Response> getCategories() {

        try {
            Pageable topTen = PageRequest.of(0, 10);
            List<Object[]> top10Categories = foodRecipeRepository.findTop10PopularCategories(topTen);
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    top10Categories,
                    "Get Category List successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }

    @Operation(summary = "Set recipe as cooked")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set recipe as cooked successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Set recipe as cooked failed")})
    @PutMapping("/cooked-recipe")
    public ResponseEntity<Response> setRecipeAsCooked(@RequestBody SetCookedRecipeDto dto) {

        try {
            Integer userId = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();
            foodRecipeService.setRecipeAsCooked(userId, dto);
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    new ArrayList<>(),
                    "Set recipe as cooked successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }

    @Operation(summary = "Get cooked recipes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get cooked recipes successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Get cooked recipes failed")})
    @GetMapping("/cooked-recipe")
    public ResponseEntity<Response> getCookedRecipe() {

        try {
            Integer userId = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    foodRecipeService.getCookedRecipesByUser(userId),
                    "Get cooked recipes successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }
}
