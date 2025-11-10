// Purpose: CommandLineRunner that loads recipe data from classpath `data/recipes.json` into the DB
// at application startup (disabled under the 'test' profile). Duplicates removed and fixed rating precision.
package com.example.recipes.loader;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.recipes.model.Recipe;
import com.example.recipes.repository.RecipeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final RecipeRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();

    private final List<String> cuisines = Arrays.asList(
        "Italian", "Chinese", "Mexican", "Indian", "French",
        "Japanese", "Thai", "Mediterranean", "American", "Greek"
    );

    private final List<String> recipeTypes = Arrays.asList(
        "Salad", "Soup", "Stew", "Roast", "Grill",
        "Bake", "Fry", "Steam", "Broil", "Braise"
    );

    public DataLoader(RecipeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        ClassPathResource resource = new ClassPathResource("data/recipes.json");
        if (!resource.exists()) return;

        // Use a set to avoid duplicate titles
        Set<String> addedTitles = new HashSet<>();

        try (InputStream is = resource.getInputStream()) {
            JsonNode root = mapper.readTree(is);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    Recipe recipe = new Recipe();

                    // Generate unique random title
                    String title;
                    do {
                        title = recipeTypes.get(random.nextInt(recipeTypes.size())) + " " + random.nextInt(100);
                    } while (!addedTitles.add(title)); // ensures no duplicates

                    recipe.setTitle(title);
                    recipe.setCuisine(cuisines.get(random.nextInt(cuisines.size())));

                    // Generate rating between 3.0 and 5.0 (one decimal only)
                    double rating = 3.0 + (random.nextDouble() * 2.0); // 3.0 to 5.0
                    rating = BigDecimal.valueOf(rating)
                            .setScale(1, RoundingMode.HALF_UP)
                            .doubleValue();
                    recipe.setRating(rating);

                    // Random times and servings
                    int prepTime = 15 + random.nextInt(46);
                    int cookTime = 20 + random.nextInt(41);
                    recipe.setPrepTime(prepTime);
                    recipe.setCookTime(cookTime);
                    recipe.setTotalTime(prepTime + cookTime);
                    recipe.setServes(String.format("%d-%d", 2 + random.nextInt(3), 4 + random.nextInt(5)));

                    recipe.setDescription(textOrNull(node, "description"));
                    recipe.setNutrients(node.get("nutrients"));

                    // Extract numeric calories
                    Integer caloriesInt = extractCalories(node);
                    recipe.setCaloriesInt(caloriesInt);

                    repository.save(recipe);
                }
            }
        }
    }

    private Integer extractCalories(JsonNode node) {
        try {
            JsonNode nutrients = node.get("nutrients");
            if (nutrients != null && nutrients.get("calories") != null) {
                String cal = nutrients.get("calories").asText();
                String digits = cal.replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) {
                    return Integer.parseInt(digits);
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode n = node.get(field);
        if (n == null || n.isNull()) return null;
        String t = n.asText().trim();
        if (t.isEmpty() || t.equalsIgnoreCase("nan") || t.equalsIgnoreCase("na")) return null;
        return t;
    }
}
