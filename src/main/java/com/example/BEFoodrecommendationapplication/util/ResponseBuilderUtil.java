package com.example.BEFoodrecommendationapplication.util;

import com.example.BEFoodrecommendationapplication.dto.Response;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ResponseBuilderUtil {

    public static Response responseBuilder (Object data, String message, StatusCode statusCode) {
        Response response = Response.builder()
                .statusCode(statusCode.getCode())
                .message(message)
                .data(data)
                .build();
        return response;
    }
}
