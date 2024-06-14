package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.dto.SetPasswordRequest;
import com.example.BEFoodrecommendationapplication.dto.UserDto;
import com.example.BEFoodrecommendationapplication.dto.UserInput;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.service.FoodRecipe.FoodRecipeService;
import com.example.BEFoodrecommendationapplication.service.User.AuthenticationService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin("${allowed.origins}")
@Tag(name = "User")
public class UserController {

    private final UserService userService;
    private final FoodRecipeService foodRecipeService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Set user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set profile successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "404", description = "Set profile failed")})
    @PutMapping("/me")
    public ResponseEntity<Response> setUserProfile(@RequestBody UserInput userInput) {
        try {
            Integer id = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();
            User user = userService.save(id, userInput);
            UserDto newUser = userService.mapUserToUserDto(user);
            newUser.setCondition(userInput.getCondition());
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    newUser,
                    "Set profile successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }


    @Operation(summary = "Get user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get user successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "404", description = "Get user failed")})
    @GetMapping("user/{id}")
    public ResponseEntity<Response> getUser(@PathVariable Integer id) {
        try {

            UserDto user = userService.getUser(id);
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    user,
                    "Get user successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }

    @Operation(summary = "Get user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get user successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "404", description = "Get user failed")})
    @GetMapping("/me")
    public ResponseEntity<Response> getInfo() {
        try {

            Integer id = AuthenticationUtils.getUserFromSecurityContext().getId();
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    userService.getUser(id),
                    "Get user successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }

    @Operation(summary = "Upload avatar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upload successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "404", description = "Upload failed")})
    @PostMapping("/avatar")
    public ResponseEntity<Response> uploadFile(@RequestParam("image") MultipartFile multipartFile) throws IOException {

        try {
            Integer id = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    userService.uploadAvatar(multipartFile, id),
                    "Upload successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }

    }

    @Operation(summary = "Save recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Save recipe successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "500", description = "Save recipe failed")})
    @PostMapping("/saved-recipe")
    public ResponseEntity<Response> saveRecipeForUser(@RequestParam int foodId, @RequestParam(defaultValue = "true") boolean save) {

        try {
            Integer userId = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();

            userService.saveOrDeleteRecipeForUser(userService.findById(userId), foodRecipeService.findById(foodId), save);
            String message = save ? "Saved successfully" : "Deleted successfully";

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    new ArrayList<>(),
                    message,
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR));

        }


    }

    @Operation(summary = "Get recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get recipe successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )}),
            @ApiResponse(responseCode = "404", description = "Get recipe failed")})
    @GetMapping("/saved-recipe")
    public ResponseEntity<Response> getSaveRecipeForUser() {

        try {
            Integer userId = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();


            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    userService.getSavedRecipesByUser(userId),
                    "Get recipe successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }


    }

    @Operation(summary = "Set password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set password successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Set password failed")})
    @PutMapping("/change-password")
    public ResponseEntity<Response> changePassword(
            @RequestBody SetPasswordRequest request
    ) {
        try {

            Integer userId = Objects.requireNonNull(AuthenticationUtils.getUserFromSecurityContext()).getId();
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    authenticationService.setPassword(userId, request.getCurrentPassword(), request.getNewPassword(), request.getConfirmPassword()),
                    "Set password successfully",
                    StatusCode.SUCCESS));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.BAD_REQUEST));
        }

    }
}
