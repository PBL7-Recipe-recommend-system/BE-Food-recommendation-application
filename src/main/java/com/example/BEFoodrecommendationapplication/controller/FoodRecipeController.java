package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.dto.SearchResult;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.RecentSearch;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.repository.FoodRecipeRepository;
import com.example.BEFoodrecommendationapplication.repository.RecentSearchRepository;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import com.example.BEFoodrecommendationapplication.service.FoodRecipe.FoodRecipeService;
import com.example.BEFoodrecommendationapplication.util.AuthenticationUtils;
import com.example.BEFoodrecommendationapplication.util.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
@Tag(name="Food Recipe")
public class FoodRecipeController {
    private final FoodRecipeService foodRecipeService;

    private final UserRepository userRepository;

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
                                           @RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size){
        try {

            Page<SearchResult> listRecipes = foodRecipeService.search(name, category, rating, PageRequest.of(page, size));
            Response response = Response.builder()
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message("Search Recipe successfully")
                    .data(listRecipes)
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.NOT_FOUND.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);

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
    @GetMapping("get-detail/{id}")
    @Cacheable("getDetail")
    public ResponseEntity<Response> getRecipeById(@PathVariable Integer id) {
        try {

            FoodRecipe foodRecipe = foodRecipeService.findById(id);
            Integer userId = AuthenticationUtils.getUserFromSecurityContext().getId();
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

            Response response = Response.builder()
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message("Get Detail successfully")
                    .data(foodRecipeService.mapToDto(foodRecipe))
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.NOT_FOUND.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);

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
    @GetMapping("/recent-search")
    @Cacheable("recentSearch")
    public ResponseEntity<Response> getRecentViews() {
        try {

            List<SearchResult> recentSearches = recentSearchRepository
                    .findTop10ByUser_IdOrderByTimestampDesc(AuthenticationUtils.getUserFromSecurityContext().getId())
                    .stream()
                    .map(RecentSearch::getRecipe)
                    .map(foodRecipeService::mapToSearchResult)
                    .toList();
            Response response = Response.builder()
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message("Get Recent Search successfully")
                    .data(recentSearches)
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.NOT_FOUND.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);

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
    @GetMapping("get-popular")
    @Cacheable("getPopular")
    public ResponseEntity<Response> getPopularRecipes( @RequestParam Integer page,
                                                       @RequestParam Integer size) {
        try {

            Page<SearchResult> popularRecipes = foodRecipeService.findPopularRecipes(page,size);

            Response response = Response.builder()
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message("Get popular recipes successfully")
                    .data(popularRecipes)
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.NOT_FOUND.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);

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
    @GetMapping("/get-category-list")
    @Cacheable("getCategory")
    public  ResponseEntity<Response> getCategories() {

        try {

            Response response = Response.builder()
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message("Get Category List successfully")
                    .data(foodRecipeRepository.findDistinctCategories())
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.NOT_FOUND.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);

        }
    }
}
