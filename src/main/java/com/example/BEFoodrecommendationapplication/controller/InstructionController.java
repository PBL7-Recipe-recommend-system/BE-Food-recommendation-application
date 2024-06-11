package com.example.BEFoodrecommendationapplication.controller;


import com.example.BEFoodrecommendationapplication.dto.Response;
import com.example.BEFoodrecommendationapplication.dto.UpdateInstructionRequest;
import com.example.BEFoodrecommendationapplication.exception.RecordNotFoundException;
import com.example.BEFoodrecommendationapplication.service.FoodRecipe.FoodRecipeService;
import com.example.BEFoodrecommendationapplication.util.ResponseBuilderUtil;
import com.example.BEFoodrecommendationapplication.util.StatusCode;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/instruction")
@RequiredArgsConstructor
@CrossOrigin("${allowed.origins}")
@Tag(name = "Instruction")
public class InstructionController {
    private final FoodRecipeService foodRecipeService;

    @Operation(summary = "Get Instruction by recipe id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Instruction by recipe id successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Get Instruction by recipe id failed")})
    @GetMapping("/{recipeId}")
    public ResponseEntity<Response> getInstructionByRecipeId(@PathVariable Integer recipeId) {
        try {

            List<String> instruction = foodRecipeService.getRecipeInstructionById(recipeId);

            return ResponseEntity.ok(ResponseBuilderUtil.responseBuilder(
                    instruction,
                    "Get Instruction by recipe id successfully",
                    StatusCode.SUCCESS));

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilderUtil.responseBuilder(new ArrayList<>(), e.getMessage(), StatusCode.NOT_FOUND));

        }
    }

    @Operation(summary = "Update a specific instruction of a recipe by index")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instruction updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid index provided"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PutMapping("/{recipeId}")
    public ResponseEntity<?> updateInstructionAtIndex(
            @PathVariable Integer recipeId,
            @RequestBody UpdateInstructionRequest newInstruction) {

        try {
            List<String> updatedInstructions = foodRecipeService.updateRecipeInstructionAtIndex(recipeId, newInstruction.getStep(), newInstruction.getInstruction());
            return ResponseEntity.ok().body(ResponseBuilderUtil.responseBuilder(
                    updatedInstructions,
                    "Instruction updated successfully",
                    StatusCode.SUCCESS));
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR));
        }
    }

    @Operation(summary = "Delete a specific instruction of a recipe by index")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instruction deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid index provided"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @DeleteMapping("/{recipeId}/step/{index}")
    public ResponseEntity<?> deleteInstructionAtIndex(
            @PathVariable Integer recipeId,
            @PathVariable int index) {

        try {
            List<String> updatedInstructions = foodRecipeService.deleteRecipeInstructionAtIndex(recipeId, index);
            return ResponseEntity.ok().body(ResponseBuilderUtil.responseBuilder(
                    updatedInstructions,
                    "Instruction deleted successfully",
                    StatusCode.SUCCESS));
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR));
        }
    }
    

    @Operation(summary = "Add a new instruction to a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New instruction added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PostMapping("/{recipeId}")
    public ResponseEntity<?> addInstruction(
            @PathVariable Integer recipeId,
            @RequestBody UpdateInstructionRequest newInstruction) {

        try {
            List<String> updatedInstructions = foodRecipeService.addRecipeInstruction(recipeId, newInstruction.getInstruction());
            return ResponseEntity.ok().body(ResponseBuilderUtil.responseBuilder(
                    updatedInstructions,
                    "New instruction added successfully",
                    StatusCode.SUCCESS));
        } catch (RecordNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseBuilderUtil.responseBuilder(null, e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR));
        }
    }

}
