package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.repository.IngredientRepository;
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
            @ApiResponse(responseCode = "400", description = "Get ingredients failed")})
    @GetMapping("/get-ingredients")
    public ResponseEntity<Response> getTop100Ingredients() {
        try {

            List<Ingredient> ingredients = ingredientRepository.find100Ingredients();
            Response response = Response.builder()
                    .statusCode(200)
                    .message("Get ingredients successfully")
                    .data(ingredients)
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Response errorResponse = Response.builder()
                    .statusCode(400)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);

        }
    }
}
