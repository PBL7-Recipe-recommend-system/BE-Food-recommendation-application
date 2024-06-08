package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.entity.WaterIntake;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.service.FoodRecipe.UserCookedRecipeService;
import com.example.BEFoodrecommendationapplication.service.User.UserService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/tracking/nutrition")
@RequiredArgsConstructor
@CrossOrigin("${allowed.origins}")
@Tag(name = "Tracking Nutrition")
public class NutritionTrackingController {
    private final UserCookedRecipeService userCookedRecipeService;
    private final UserService userService;

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

    @Operation(summary = "Set water intake")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Water intake updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/water-intake")
    public ResponseEntity<Response> setWaterIntake(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
            @RequestParam Float amount) {
        try {
            Integer userId = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();
            WaterIntake updatedWaterIntake = userService.updateOrCreateWaterIntake(userId, date, amount);
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    updatedWaterIntake,
                    "Water intake updated successfully",
                    StatusCode.SUCCESS));
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.BAD_REQUEST));
        }
    }
}
