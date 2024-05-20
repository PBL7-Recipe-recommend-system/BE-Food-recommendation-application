package com.example.BEFoodrecommendationapplication.util;

import com.example.BEFoodrecommendationapplication.entity.Ingredient;
import com.example.BEFoodrecommendationapplication.repository.IngredientRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseLoader implements CommandLineRunner {

    private final IngredientRepository ingredientRepository;


    @Override
    public void run(String... args) throws Exception {
//        if (ingredientRepository.count() == 0){
//            List<Ingredient> ingredients = new CsvToBeanBuilder<Ingredient>(new FileReader("src/main/java/com/example/BEFoodrecommendationapplication/data/ingredients.csv"))
//                    .withType(Ingredient.class)
//                    .withSeparator(';')
//                    .build()
//                    .parse();
//
//            ingredientRepository.saveAll(ingredients);
//        }
    }
}