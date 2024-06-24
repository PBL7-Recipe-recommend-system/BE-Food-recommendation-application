package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddRecipeMealPlanInput {

    private Integer recipeId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private String meal;

    private String description;

    private Integer dailyCalories;

}
