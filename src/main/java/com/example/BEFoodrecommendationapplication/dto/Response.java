package com.example.BEFoodrecommendationapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    @JsonProperty("Status")
    public Integer statusCode;
    @JsonProperty("Message")
    public String message;
    @JsonProperty("Data")
    public Object data;
}
