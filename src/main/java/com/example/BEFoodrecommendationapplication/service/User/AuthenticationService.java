package com.example.BEFoodrecommendationapplication.service.User;

import com.example.BEFoodrecommendationapplication.dto.AuthenticationRequest;
import com.example.BEFoodrecommendationapplication.dto.AuthenticationResponse;
import com.example.BEFoodrecommendationapplication.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    String register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    String forgotPassword(String email);

    String setPassword(String email, String newPassword);

    AuthenticationResponse verifyAccount(String email, String otp);

    String regenerateOtp(String email);
}
