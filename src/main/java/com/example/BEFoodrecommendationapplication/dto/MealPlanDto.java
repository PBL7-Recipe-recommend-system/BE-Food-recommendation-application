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

    private Object breakfast;

    private Object lunch;

    private Object dinner;

    private Object morningSnack;

    private Object afternoonSnack;

    private int mealCount;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate date;

    private Integer dailyCalories;

    private Integer totalCalories;

    private String description;

}
