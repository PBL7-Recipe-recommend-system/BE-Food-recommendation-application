package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.dto.UserInput;
import com.example.BEFoodrecommendationapplication.dto.UserResponse;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.service.User.AdminService;
import com.example.BEFoodrecommendationapplication.service.User.UserService;
import com.example.BEFoodrecommendationapplication.util.ResponseBuilderUtil;
import com.example.BEFoodrecommendationapplication.util.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin("${allowed.origins}")
@Tag(name = "Admin", description = "Admin operations")
public class AdminController {


    private final AdminService adminService;
    private final UserService userService;


    @Operation(summary = "Retrieve all users", description = "Returns a list of all users in the system, accessible only by admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of user list", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Access denied for unauthorized users")
    })
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> getAllUsers(Pageable pageable) {
        try {
            Page<UserResponse> users = adminService.findAllUsers(pageable);
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    users,
                    "Users retrieved successfully",
                    StatusCode.SUCCESS));
        } catch (Exception e) {
            // Assuming there's a possibility of a service-level exception, handle it gracefully
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseBuilderUtil.responseBuilder(
                            new ArrayList<>(),
                            "Failed to retrieve users due to an internal error",
                            StatusCode.INTERNAL_SERVER_ERROR));
        }
    }

    @Operation(summary = "Update a user", description = "Updates user details, accessible only by admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied for unauthorized users"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> updateUser(@PathVariable Integer id, @RequestBody UserInput userInput) {
        try {
            User updatedUser = userService.save(id, userInput);
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    updatedUser, "User updated successfully", StatusCode.SUCCESS));
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseBuilderUtil.responseBuilder(
                    new ArrayList<>(), "User not found", StatusCode.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseBuilderUtil.responseBuilder(
                    new ArrayList<>(), "Failed to update user due to an internal error", StatusCode.INTERNAL_SERVER_ERROR));
        }
    }

    @Operation(summary = "Retrieve a user", description = "Returns a user by their ID, accessible only by admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of user", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied for unauthorized users")
    })
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> getUserById(@PathVariable Integer id) {
        try {
            UserResponse user = adminService.findUserById(id);
            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    user,
                    "User retrieved successfully",
                    StatusCode.SUCCESS));
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseBuilderUtil.responseBuilder(
                            null,
                            "User not found",
                            StatusCode.NOT_FOUND));
        } catch (Exception e) {
            // Assuming there's a possibility of a service-level exception, handle it gracefully
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseBuilderUtil.responseBuilder(
                            null,
                            "Failed to retrieve user due to an internal error",
                            StatusCode.INTERNAL_SERVER_ERROR));
        }
    }
}

