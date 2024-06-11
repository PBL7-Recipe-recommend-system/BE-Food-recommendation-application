package com.example.BEFoodrecommendationapplication.util;

import com.example.BEFoodrecommendationapplication.dto.ShortRecipe;
import com.example.BEFoodrecommendationapplication.entity.FoodRecipe;
import com.example.BEFoodrecommendationapplication.repository.FoodRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Math.round;

@Component
@RequiredArgsConstructor
public class StringUtil {
    private final FoodRecipeRepository foodRecipeRepository;

    public List<String> splitBySlash(String input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(input.split("///"))
                .map(String::trim)  // Remove any leading or trailing whitespace
                .collect(Collectors.toList());
    }

    public List<String> splitStringToList(String input) {
        if (input == null) {
            return new ArrayList<>();
        }
        if (!input.startsWith("c")) {
            return Collections.singletonList(input.replaceAll("^\"|\"$", ""));
        }
        input = input.substring(3, input.length() - 1);
        String[] items = input.split(", ");
        for (int i = 0; i < items.length; i++) {
            items[i] = items[i].replaceAll("^\"|\"$", "").replace("\\", "").replace("\n", "").replace("\\\"", "");
        }
        return Arrays.asList(items);
    }


    public List<String> splitInstructions(String input) {

        String data = input.replace("\n", " ")
                .replace("\r", " ").replace("c(", "").replace("\")", "");
//        System.out.println(data);

        data = data.replaceAll("^\"", "");  // Remove leading quote if present.

        String[] instructions = data.split("\", ");
        Pattern spacePattern = Pattern.compile(" +");  // Regex to match one or more spaces.

        List<String> result = new ArrayList<>();
        for (String instruction : instructions) {
            Matcher matcher = spacePattern.matcher(instruction);
            String cleanedInstruction = matcher.replaceAll(" "); // Replace multiple spaces with a single space.
            cleanedInstruction = cleanedInstruction.replace("\"", "").trim();

            result.add(cleanedInstruction);
        }
//        for (String i : result) {
//            System.out.println(i);
//        }
        return result;
    }

    public List<String> partitionIntoFourParts(List<String> instructions) {
        List<String> partitioned = new ArrayList<>();
        int totalSize = instructions.size();
        int partSize = totalSize / 4;
        int remainder = totalSize % 4;

        for (int i = 0; i < 4; i++) {
            int start = i * partSize + Math.min(i, remainder);
            int end = start + partSize + (i < remainder ? 1 : 0);
            if (start < totalSize) {
                String partInstructions = String.join(" ", instructions.subList(start, end));
                partitioned.add(partInstructions);
            } else {
                partitioned.add("");
            }
        }
        return partitioned;
    }

    public String cleanTime(String time) {
        if (time == null || time.isEmpty() || !time.startsWith("PT")) {
            return time;
        }
        return time.replaceFirst("PT", "");
    }

    public Object mapToShortRecipe(Integer id) {

        if (foodRecipeRepository.findById(id).isEmpty()) {
            return new ArrayList<>();
        }
        FoodRecipe foodRecipe = foodRecipeRepository.findById(id).get();
        return ShortRecipe.builder()
                .recipeId(foodRecipe.getRecipeId())
                .image(splitStringToList(foodRecipe.getImages()).get(0))
                .totalTime(cleanTime(foodRecipe.getTotalTime()))
                .calories(round(foodRecipe.getCalories()))
                .name(foodRecipe.getName())
                .authorName(foodRecipe.getAuthor().getName())
                .rating(foodRecipe.getAggregatedRatings())
                .build();
    }


}
