package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.RecipeDto;
import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.dto.SearchRequest;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.entity.RecentSearch;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.repository.RecentSearchRepository;
import com.example.BEFoodrecommendationapplication.repository.UserRepository;
import com.example.BEFoodrecommendationapplication.service.FoodRecipe.FoodRecipeService;
import com.example.BEFoodrecommendationapplication.util.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
@Tag(name="Food Recipe")
public class FoodRecipeController {
    private final FoodRecipeService foodRecipeService;

    private final UserRepository userRepository;

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
    public ResponseEntity<Response> search(@RequestBody SearchRequest request){
        try {

            Page<RecipeDto> listRecipes = foodRecipeService.search(request.getName(), request.getCategory(), request.getRating(), PageRequest.of(request.getPage(), request.getSize()));
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
    public ResponseEntity<Response> getRecipeById(@PathVariable Integer id, @RequestParam Integer userId) {
        try {

            FoodRecipe foodRecipe = foodRecipeService.findById(id);

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
    public ResponseEntity<Response> getRecentViews(@RequestParam Integer userId) {
        try {

            List<RecipeDto> recentSearches = recentSearchRepository.findTop10ByUser_IdOrderByTimestampDesc(userId).stream()
                    .map(RecentSearch::getRecipe)
                    .map(foodRecipeService::mapToDto)
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
}
