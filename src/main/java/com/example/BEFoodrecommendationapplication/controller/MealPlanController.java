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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meal-plans")
@RequiredArgsConstructor
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
    public ResponseEntity<Response> addMealPlans(@RequestBody List<MealPlanInput> mealPlans) {

        try {
            Integer id = AuthenticationUtils.getUserFromSecurityContext().getId();


            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    mealPlanService.addMealPlans(mealPlans,id),
                    "Add meal plan successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR));

        }
    }
}