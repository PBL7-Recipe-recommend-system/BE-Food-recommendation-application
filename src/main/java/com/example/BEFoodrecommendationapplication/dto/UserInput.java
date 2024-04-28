package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInput {
    @JsonProperty("weight")
    private float weight;
    @JsonProperty("height")
    private float height;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("birthday")
    private Date birthday;
    @JsonProperty("daily activities")
    private String daily_activities;
    @JsonProperty("dietary goal")
    private String dietary_goal;
    @JsonProperty("ingredients")
    private List<String> ingredients;

    // getters and setters
}
