package com.example.BEFoodrecommendationapplication.controller;


import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.service.FoodRecipe.FoodRecipeService;
import com.example.BEFoodrecommendationapplication.service.Ingredient.IngredientService;
import com.example.BEFoodrecommendationapplication.util.ResponseBuilderUtil;
import com.example.BEFoodrecommendationapplication.util.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/instruction")
@RequiredArgsConstructor
@CrossOrigin("${allowed.origins}")
@Tag(name="Instruction")
public class InstructionController {
    private final FoodRecipeService foodRecipeService;

    @Operation(summary = "Get Instruction by recipe id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Instruction by recipe id successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Get Instruction by recipe id failed")})
    @GetMapping("/{recipeId}")
    public ResponseEntity<Response> getInstructionByRecipeId(@PathVariable Integer recipeId) {
        try {

            List<String> instruction = foodRecipeService.getRecipeInstructionById(recipeId);

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    instruction,
                    "Get Instruction by recipe id successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }
}
