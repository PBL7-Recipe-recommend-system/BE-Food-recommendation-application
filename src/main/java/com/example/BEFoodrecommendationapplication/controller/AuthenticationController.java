package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.*;
import com.example.BEFoodrecommendationapplication.exception.*;
import com.example.BEFoodrecommendationapplication.service.User.AuthenticationService;
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

            return ResponseEntity.ok(
                    ResponseBuilderUtil.responseBuilder(
                            null,
                                service.register(request),
                                StatusCode.SUCCESS)
            );

        }catch (DuplicateDataException | InvalidEmailException | WrongFormatPasswordException e){


            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.UNAUTHORIZED));
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

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    service.authenticate(request),
                    "Authenticate success",
                    StatusCode.SUCCESS));

        }catch (InvalidEmailException | RecordNotFoundException e){


            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.UNAUTHORIZED));
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


            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    null,
                    service.forgotPassword(email),
                    StatusCode.SUCCESS));

        }catch (ErrorException | RecordNotFoundException e){

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.BAD_REQUEST));
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
    @PostMapping("/otp-verification")
    public ResponseEntity<Response> verifyAccount(@RequestParam String email,
                                                @RequestBody OtpRequest otp) {
        try {

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    null,
                    service.verifyAccount(email, otp.getOtp()),
                    StatusCode.SUCCESS));
        }catch (RecordNotFoundException e){

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.BAD_REQUEST));
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

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    null,
                    service.regenerateOtp(email),
                    StatusCode.SUCCESS));
        }catch (RecordNotFoundException e){

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.BAD_REQUEST));
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
            @RequestParam String email, @RequestBody SetPasswordRequest request
    ) {
        try {


            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    service.setPassword(email, request.getNewPassword()),
                    "Set password successfully",
                    StatusCode.SUCCESS));
        }catch (RecordNotFoundException e){

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.BAD_REQUEST));
        }

    }

}