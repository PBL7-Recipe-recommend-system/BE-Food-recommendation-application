package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.repository.IngredientRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingredients")
@RequiredArgsConstructor
@Tag(name="Ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepository;


    @Operation(summary = "Get ingredients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get ingredients successfully",
                    content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(responseCode = "404", description = "Get ingredients failed")})
    @GetMapping("/ingredients")
    public ResponseEntity<Response> getTop100Ingredients() {
        try {

            List<Ingredient> ingredients = ingredientRepository.find100Ingredients();

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    ingredients,
                    "Get ingredients successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.NOT_FOUND));

        }
    }
}
