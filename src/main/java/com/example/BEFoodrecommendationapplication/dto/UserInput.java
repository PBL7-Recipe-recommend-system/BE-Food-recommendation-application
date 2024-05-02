package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
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

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate birthday;

    @JsonProperty("dailyActivities")
    private String daily_activities;

    @JsonProperty("meals")
    private Integer meals;

    @JsonProperty("dietaryGoal")
    private Integer dietary_goal;

    @JsonProperty("ingredients")
    private List<String> ingredients;

}
