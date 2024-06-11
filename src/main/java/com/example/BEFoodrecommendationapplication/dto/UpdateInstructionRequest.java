package com.example.BEFoodrecommendationapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInstructionRequest {
    private Integer step;
    private String instruction;
}
