package com.example.BEFoodrecommendationapplication.controller;

import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.dto.UserInput;
import com.example.BEFoodrecommendationapplication.entity.User;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name="Ingredients")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Set user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set profile successfully",
                    content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )}),
            @ApiResponse(responseCode = "400", description = "Set profile failed")})
    @PutMapping("set-profile/{id}")
    public ResponseEntity<Response> setUserProfile(@PathVariable Integer id, @RequestBody UserInput userInput) {
        try {

            User user = userService.save(id, userInput);
            Response response = Response.builder()
                    .statusCode(200)
                    .message("Set profile successfully")
                    .data(user)
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Response errorResponse = Response.builder()
                    .statusCode(400)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        }
    }
}
