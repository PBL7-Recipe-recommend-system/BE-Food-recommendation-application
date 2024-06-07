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

    private Integer breakfast;

    private Integer lunch;

    private Integer dinner;

    private Integer morningSnack;

    private Integer afternoonSnack;

    private int mealCount;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private Integer dailyCalories;

    private Integer totalCalories;

    private String description;

}
