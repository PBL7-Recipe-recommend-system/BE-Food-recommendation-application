package com.example.BEFoodrecommendationapplication.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetPasswordRequest {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
