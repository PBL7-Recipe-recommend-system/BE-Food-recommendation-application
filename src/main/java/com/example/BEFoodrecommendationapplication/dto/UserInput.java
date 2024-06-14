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

    @JsonProperty("name")
    private String name;

    @JsonProperty("weight")
    private float weight;

    @JsonProperty("height")
    private float height;

    @JsonProperty("gender")
    private String gender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthday;

    @JsonProperty("dailyActivities")
    private String dailyActivities;

    @JsonProperty("meals")
    private Integer meals;

    @JsonProperty("dietaryGoal")
    private Integer dietaryGoal;

    @JsonProperty("condition")
    private String condition;

    @JsonProperty("ingredients")
    private List<String> ingredients;


}
