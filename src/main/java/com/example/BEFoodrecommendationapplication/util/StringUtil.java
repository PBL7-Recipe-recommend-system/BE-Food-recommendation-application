package com.example.BEFoodrecommendationapplication.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@Component
@NoArgsConstructor
public class StringUtil {
    public List<String> splitStringToList(String input) {
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
}
