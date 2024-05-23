package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("weight")
    private float weight;

    @JsonProperty("height")
    private float height;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("dailyActivities")
    private String dailyActivities;

    @JsonProperty("meals")
    private Integer meals;

    @JsonProperty("dietaryGoal")
    private Integer dietaryGoal;

    @JsonProperty("bmi")
    private Float bmi;

    @JsonProperty("isCustomPlan")
    private Boolean isCustomPlan;

    @JsonProperty("recommendCalories")
    private Integer recommendCalories;

}
