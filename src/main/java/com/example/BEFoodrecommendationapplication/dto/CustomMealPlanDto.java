package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomMealPlanDto {

    private List<Object> breakfast;
    private List<Object> lunch;
    private List<Object> dinner;
    private List<Object> morningSnack;
    private List<Object> afternoonSnack;

    private int mealCount;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private Integer dailyCalories;
    private Integer totalCalories;
    private Integer totalCaloriesPercentage;
    private Integer totalProteinPercentage;
    private Integer totalFatPercentage;
    private String description;

}
