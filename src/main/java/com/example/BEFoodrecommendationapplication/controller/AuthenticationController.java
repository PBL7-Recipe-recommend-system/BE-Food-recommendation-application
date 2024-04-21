package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.AuthenticationRequest;
import com.example.BEFoodrecommendationapplication.dto.AuthenticationResponse;
import com.example.BEFoodrecommendationapplication.dto.RegisterRequest;
import com.example.BEFoodrecommendationapplication.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @PutMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestParam String email
    ) {
        return new ResponseEntity<>(service.forgotPassword(email), HttpStatus.OK);
    }
    @PutMapping("/set-password")
    public ResponseEntity<String> setPassword(
            @RequestParam String email, @RequestHeader String newPassword
    ) {
        return new ResponseEntity<>(service.setPassword(email, newPassword), HttpStatus.OK);
    }

}