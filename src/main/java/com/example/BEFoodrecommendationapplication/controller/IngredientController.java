package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.IngredientDto;
import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.dto.UpdateIngredientsRequest;
import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.repository.IngredientRepository;
import com.example.BEFoodrecommendationapplication.service.Ingredient.IngredientService;
import com.example.BEFoodrecommendationapplication.util.ResponseBuilderUtil;
import com.example.BEFoodrecommendationapplication.util.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
@CrossOrigin("${allowed.origins}")
@Tag(name="Ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepository;

    private final IngredientService ingredientService;

    @Operation(summary = "Get ingredients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get ingredients successfully",
                    content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(responseCode = "404", description = "Get ingredients failed")})
    @GetMapping("")
    public ResponseEntity<Response> getTop100Ingredients() {
        try {

            List<Ingredient> ingredients = ingredientRepository.find100Ingredients();

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    ingredients,
                    "Get ingredients successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }

    @Operation(summary = "Get ingredients by recipe id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get ingredients by recipe id successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Get ingredients by recipe id failed")})
    @GetMapping("/recipe/{id}")
    public ResponseEntity<Response> getRecipeIngredientsById(@PathVariable Integer id) {
        try {

            List<IngredientDto> ingredients = ingredientService.getRecipeIngredientsById(id);

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    ingredients,
                    "Get ingredients by recipe id successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }

    @Operation(summary = "Update ingredients by recipe id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update ingredients by recipe id successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Update ingredients by recipe id failed")})
    @PutMapping("/recipe/{recipeId}")
    public ResponseEntity<Response> updateIngredientsByRecipeId(@PathVariable Integer recipeId, @RequestBody UpdateIngredientsRequest request) {
        try {

            UpdateIngredientsRequest updatedRecipe = ingredientService.updateRecipeIngredientNames(recipeId, request);

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    updatedRecipe,
                    "Update ingredients by recipe id successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }
}
