package com.example.BEFoodrecommendationapplication.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    private String name;

    private String category;

    private Integer rating;

    private int page = 0;

    private int size = 10;
}
