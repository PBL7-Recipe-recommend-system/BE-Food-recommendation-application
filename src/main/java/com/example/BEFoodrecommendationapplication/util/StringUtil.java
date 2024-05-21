package com.example.BEFoodrecommendationapplication.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@Component
@NoArgsConstructor
public class StringUtil {
    public List<String> splitStringToList(String input) {
        if(input == null)
        {
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


        if (input.length() > 2) {
            input = input.substring(2, input.length() - 2);
        }
        input = input.replace("\\\"", "\"");

        input = input.replace("\\r\\n", " ").replace("\\n", " ").replace("\\r", " ");
        return new ArrayList<>(Arrays.asList(input.split("\", \"")));
    }

    public List<String> partitionIntoFourParts(List<String> instructions) {
        List<String> partitioned = new ArrayList<>();
        int totalSize = instructions.size();
        int partSize = totalSize / 4;
        int remainder = totalSize % 4;

        for (int i = 0; i < 4; i++) {
            int start = i * partSize + Math.min(i, remainder);
            int end = start + partSize + (i < remainder ? 1 : 0);
            if (start < totalSize) {  // Check to ensure we do not go out of list's bounds
                String partInstructions = String.join(" ", instructions.subList(start, end));
                partitioned.add(partInstructions);
            } else {
                partitioned.add(""); // Add an empty string if there are no instructions to distribute
            }
        }
        return partitioned;
    }
}
