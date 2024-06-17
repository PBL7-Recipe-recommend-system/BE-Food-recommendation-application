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
public class UserDto {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @JsonProperty("birthday")
    private LocalDate birthday;

    @JsonProperty("weight")
    private float weight;

    @JsonProperty("height")
    private float height;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("dailyActivities")
    private String dailyActivities;

    @JsonProperty("meals")
    private Integer meals;

    @JsonProperty("dietaryGoal")
    private Integer dietaryGoal;


    @JsonProperty("bmi")
    private Float bmi;

    @JsonProperty("condition")
    private String condition;

    @JsonProperty("isCustomPlan")
    private Boolean isCustomPlan;

    @JsonProperty("recommendCalories")
    private Integer recommendCalories;

    @JsonProperty("excludeIngredients")
    private List<String> excludeIngredients;

    @JsonProperty("includeIngredients")
    private List<String> includeIngredients;

}
