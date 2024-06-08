package com.example.BEFoodrecommendationapplication.dto;

import com.example.BEFoodrecommendationapplication.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("role")
    private Role role;

}

