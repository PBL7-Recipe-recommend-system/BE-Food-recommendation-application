package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.MealPlanInput;
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
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/meal-plans")
@RequiredArgsConstructor
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
    public ResponseEntity<Response> addMealPlans(@RequestBody MealPlanInput mealPlan) {

        try {
            Integer id = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();


            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    mealPlanService.addMealPlans(mealPlan, id),
                    "Adds meal plan successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR));

        }
    }

    @Operation(summary = "Update meal plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update meal plan successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "500", description = "Update meal plan failed")})
    @PutMapping
    public ResponseEntity<Response> editMealPlans(@RequestBody List<MealPlanInput> mealPlans) {

        try {
            Integer id = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();


            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    mealPlanService.editMealPlans(mealPlans, id),
                    "Update meal plan successfully",
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
    public ResponseEntity<Response> deleteRecipesInMealPlan(@RequestBody MealPlanInput mealPlan) {

        try {
            Integer id = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();


            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    mealPlanService.deleteRecipeInMealPlan(id, mealPlan),
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
                    mealPlanService.getCurrentMealPlans(id),
                    "Get meal plan successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }
}