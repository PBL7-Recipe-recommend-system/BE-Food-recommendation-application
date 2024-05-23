package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealPlanDto {

    private ShortRecipe breakfast;

    private ShortRecipe lunch;

    private ShortRecipe dinner;

    private ShortRecipe snack1;

    private ShortRecipe snack2;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate date;

    private Integer dailyCalorie;

    private Integer totalCalorie;

    private String description;

}