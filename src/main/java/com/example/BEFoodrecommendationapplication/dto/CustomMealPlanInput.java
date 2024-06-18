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
public class CustomMealPlanInput {

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    
    private String description;

    private Integer dailyCalories;

    private Integer totalCalories;

    private List<Integer> breakfastDishIds;

    private List<Integer> lunchDishIds;

    private List<Integer> dinnerDishIds;

    private List<Integer> morningSnackDishIds;

    private List<Integer> afternoonSnackDishIds;

}
