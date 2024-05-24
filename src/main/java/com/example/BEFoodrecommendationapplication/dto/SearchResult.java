package com.example.BEFoodrecommendationapplication.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {

    private Integer recipeId;

    private String name;

    private Integer rating;

    private String authorName;

    private String images;

    private Float calories;

    private String totalTime;
}
