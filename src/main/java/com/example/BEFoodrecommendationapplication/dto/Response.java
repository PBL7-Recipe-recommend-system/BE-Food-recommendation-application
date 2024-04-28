package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    @JsonProperty("status")
    public Integer statusCode;
    @JsonProperty("message")
    public String message;
    @JsonProperty("data")
    public Object data;
}
