package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.AuthenticationRequest;
import com.example.BEFoodrecommendationapplication.dto.AuthenticationResponse;
import com.example.BEFoodrecommendationapplication.dto.RegisterRequest;
import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.exception.*;
import com.example.BEFoodrecommendationapplication.service.User.AuthenticationService;
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
            @ApiResponse(responseCode = "401", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<Response> register(
            @RequestBody RegisterRequest request
    ) {
        try {

            Response response = Response.builder()
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message(service.register(request))
                    .data(null)
                    .build();
            return ResponseEntity.ok(response);

        }catch (DuplicateDataException | InvalidEmailException | WrongFormatPasswordException e){

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.UNAUTHORIZED.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
        }

    }

    @Operation(summary = "Authenticate user to get access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication Success",
                    content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = "Invalid Email or password")})
    @PostMapping("/authenticate")
    public ResponseEntity<Response> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {

            Response response = Response.builder()
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message("Authenticate successfully")
                    .data(service.authenticate(request))
                    .build();
            return ResponseEntity.ok(response);

        }catch (InvalidEmailException | RecordNotFoundException e){

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.UNAUTHORIZED.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
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
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message(service.forgotPassword(email))
                    .data(null)
                    .build();
            return ResponseEntity.ok(response);

        }catch (ErrorException | RecordNotFoundException e){

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.BAD_REQUEST.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
        }

    }
    @Operation(summary = "Verify account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verify account successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Verify account failed")})
    @PutMapping("/verify-account")
    public ResponseEntity<Response> verifyAccount(@RequestParam String email,
                                                @RequestParam String otp) {
        try {

            Response response = Response.builder()
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message("Verify successfully")
                    .data(service.verifyAccount(email, otp))
                    .build();

            return ResponseEntity.ok(response);
        }catch (RecordNotFoundException e){

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.BAD_REQUEST.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
        }
    }

    @Operation(summary = "Regenerate otp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Regenerate otp successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Regenerate otp failed")})
    @PutMapping("/regenerate-otp")
    public ResponseEntity<Response> regenerateOtp(@RequestParam String email) {
        try {

            Response response = Response.builder()
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message(service.regenerateOtp(email))
                    .data(null)
                    .build();

            return ResponseEntity.ok(response);
        }catch (RecordNotFoundException e){

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.BAD_REQUEST.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
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
                    .statusCode(StatusCode.SUCCESS.getCode())
                    .message(service.setPassword(email, newPassword))
                    .data(null)
                    .build();

            return ResponseEntity.ok(response);
        }catch (RecordNotFoundException e){

            Response errorResponse = Response.builder()
                    .statusCode(StatusCode.BAD_REQUEST.getCode())
                    .message(e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
        }

    }

}