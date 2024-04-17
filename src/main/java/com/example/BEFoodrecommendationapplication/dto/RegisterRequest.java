package com.example.BEFoodrecommendationapplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String name;
    @NotBlank(message = "email required")
    private String email;
    @NotBlank(message = "Password required")
    private String password;

}
