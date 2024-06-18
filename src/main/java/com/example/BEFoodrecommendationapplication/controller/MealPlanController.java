package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.AddRecipeMealPlanInput;
import com.example.BEFoodrecommendationapplication.dto.CustomMealPlanInput;
import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.service.MealPlan.MealPlanService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/meal-plans")
@RequiredArgsConstructor
@CrossOrigin("${allowed.origins}")
@Tag(name = "Meal Plan")
public class MealPlanController {


    private final MealPlanService mealPlanService;
    private final UserService userService;

    @Operation(summary = "Add meal plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add meal plan successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "500", description = "Add meal plan failed")})
    @PostMapping
    public ResponseEntity<Response> addMealPlans(@RequestBody CustomMealPlanInput mealPlan) {

        try {
            Integer id = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();

            mealPlanService.addCustomMealPlan(mealPlan, id);
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    new ArrayList<>(),
                    "Adds meal plan successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR));

        }
    }

    @Operation(summary = "Add recipe to meal plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add recipe to meal plan successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "500", description = "Add recipe to meal plan failed")})
    @PutMapping
    public ResponseEntity<Response> editMealPlans(@RequestBody AddRecipeMealPlanInput mealPlans) {

        try {
            Integer id = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();
            mealPlanService.addRecipeToMealPlan(mealPlans, id);

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    new ArrayList<>(),
                    "Add recipe to meal plan successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR));

        }
    }

    @Operation(summary = "Delete recipes in meal plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete recipes in meal plan successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "500", description = "Delete recipes in meal plan failed")})
    @PutMapping("/delete-recipes")
    public ResponseEntity<Response> deleteRecipesInMealPlan(@RequestBody AddRecipeMealPlanInput mealPlan) {

        try {
            Integer id = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();

            mealPlanService.removeRecipeFromMealPlan(mealPlan, id);
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    new ArrayList<>(),
                    "Delete recipes in meal plan successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR));

        }
    }

    @Operation(summary = "Get meal plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get meal plan successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "404", description = "Get meal plan failed")})
    @GetMapping
    public ResponseEntity<Response> getMealPlans() {

        try {
            Integer id = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();


            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    mealPlanService.getCustomMealPlans(id),
                    "Get meal plan successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }
}