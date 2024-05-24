package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealPlanInput {

    private int breakfast;

    private int lunch;

    private int dinner;

    private int brunch;

    private int snack;

    private int mealCount;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate date;

    private Integer dailyCalorie;

    private Integer totalCalorie;

    private String description;

}
