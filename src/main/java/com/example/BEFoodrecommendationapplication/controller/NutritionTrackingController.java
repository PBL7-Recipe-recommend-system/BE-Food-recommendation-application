package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.service.FoodRecipe.UserCookedRecipeService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/tracking/nutrition")
@RequiredArgsConstructor
@Tag(name = "Tracking Nutrition")
public class NutritionTrackingController {
    private final UserCookedRecipeService userCookedRecipeService;

    @Operation(summary = "Get nutrition tracking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get nutrition tracking successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Get nutrition tracking failed")})
    @GetMapping("")
    public ResponseEntity<Response> getNutrition(@RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) {
        try {

            Integer id = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    userCookedRecipeService.getDailyNutrition(id, date),
                    "Get nutrition tracking successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }
}
