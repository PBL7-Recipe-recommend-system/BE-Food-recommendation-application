package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.AuthenticationRequest;
import com.example.BEFoodrecommendationapplication.dto.AuthenticationResponse;
import com.example.BEFoodrecommendationapplication.dto.RegisterRequest;
import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.exception.*;
import com.example.BEFoodrecommendationapplication.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name="Authentication")
public class AuthenticationController {
    private final AuthenticationService service;

    @Operation(summary = "Create user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create user Success",
                    content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<Response> register(
            @RequestBody RegisterRequest request
    ) {
        try {

            Response response = Response.builder()
                    .statusCode(200)
                    .message("Register successfully")
                    .data(service.register(request))
                    .build();
            return ResponseEntity.ok(response);

        }catch (DuplicateDataException | InvalidEmailException | WrongFormatPasswordException e){

            Response errorResponse = Response.builder()
                    .statusCode(400)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }

    @Operation(summary = "Authenticate user to get access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication Success",
                    content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid Email or password")})
    @PostMapping("/authenticate")
    public ResponseEntity<Response> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {

            Response response = Response.builder()
                    .statusCode(200)
                    .message("Authenticate successfully")
                    .data(service.authenticate(request))
                    .build();
            return ResponseEntity.ok(response);

        }catch (InvalidEmailException | RecordNotFoundException e){

            Response errorResponse = Response.builder()
                    .statusCode(400)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Operation(summary = "Forgot password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Send email successfully",
                    content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))
            }),
            @ApiResponse(responseCode = "400", description = "Send email failed")})
    @PutMapping("/forgot-password")
    public ResponseEntity<Response> forgotPassword(
            @RequestParam String email
    ) {
        try {

            Response response = Response.builder()
                    .statusCode(200)
                    .message(service.forgotPassword(email))
                    .data(null)
                    .build();
            return ResponseEntity.ok(response);

        }catch (ErrorException | RecordNotFoundException e){

            Response errorResponse = Response.builder()
                    .statusCode(400)
                    .message(e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
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
    @PutMapping("/set-password")
    public ResponseEntity<Response> setPassword(
            @RequestParam String email, @RequestHeader String newPassword
    ) {
        try {

            Response response = Response.builder()
                    .statusCode(200)
                    .message(service.setPassword(email, newPassword))
                    .data(null)
                    .build();

            return ResponseEntity.ok(response);
        }catch (RecordNotFoundException e){

            Response errorResponse = Response.builder()
                    .statusCode(400)
                    .message(e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }

}