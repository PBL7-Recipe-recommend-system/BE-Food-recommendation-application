package com.example.BEFoodrecommendationapplication.controller;


import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.util.ResponseBuilderUtil;
import com.example.BEFoodrecommendationapplication.util.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HelloWorld {
    @Operation(summary = "Hello world")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hello world",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Error")})
    @GetMapping("/hello")
    public ResponseEntity<Response> helloWorld() {


            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder("Hello world", "Hello world", StatusCode.SUCCESS));

    }
}
