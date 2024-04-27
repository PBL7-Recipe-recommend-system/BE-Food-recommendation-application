package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String accessToken;
}

