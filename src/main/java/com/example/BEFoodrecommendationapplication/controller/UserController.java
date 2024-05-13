package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.dto.UserInput;
import com.example.BEFoodrecommendationapplication.entity.User;
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

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name="Ingredients")
public class UserController {

    private final UserService userService;

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
            Integer id = AuthenticationUtils.getUserFromSecurityContext().getId();
            User user = userService.save(id, userInput);

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    user,
                    "Set profile successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.NOT_FOUND));

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

            User user = userService.getUser(id);
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    user,
                    "Get user successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.NOT_FOUND));

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

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.NOT_FOUND));

        }
    }
}
