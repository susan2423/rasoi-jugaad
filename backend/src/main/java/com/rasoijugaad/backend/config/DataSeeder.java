package com.rasoijugaad.backend.config;

import com.rasoijugaad.backend.model.Recipe;
import com.rasoijugaad.backend.model.RecipeIngredient;
import com.rasoijugaad.backend.repository.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final RecipeRepository repo;

    public DataSeeder(RecipeRepository repo) {
        this.repo = repo;
    }

    private Recipe recipe(String title, String instructions, RecipeIngredient... ings) {
        Recipe r = new Recipe();
        r.setTitle(title);
        r.setInstructions(instructions);
        for (RecipeIngredient i : ings)
            r.addIngredient(i);
        return r;
    }

    private RecipeIngredient ing(String name, double qty, String unit) {
        return new RecipeIngredient(name, qty, unit);
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0)
            return;

        repo.save(recipe("Banana Bread",
                "Mash ripe bananas, mix with melted butter, sugar and egg. Fold in flour and baking soda. Bake at 175C for 50 minutes.",
                ing("banana", 3, "pcs"), ing("flour", 1.5, "cup"), ing("sugar", 0.75, "cup"),
                ing("egg", 1, "pcs"), ing("butter", 0.33, "cup"), ing("baking soda", 1, "tsp")));

        repo.save(recipe("Banana Smoothie",
                "Blend banana with cold milk and sugar until smooth.",
                ing("banana", 2, "pcs"), ing("milk", 1, "cup"), ing("sugar", 1, "tbsp")));

        repo.save(recipe("Masala Omelette",
                "Beat eggs with chopped onion, tomato, green chilli and salt. Cook on a hot oiled pan.",
                ing("egg", 3, "pcs"), ing("onion", 1, "pcs"), ing("tomato", 1, "pcs"),
                ing("green chilli", 1, "pcs"), ing("salt", 1, "tsp")));

        repo.save(recipe("Aloo Jeera",
                "Temper cumin in oil, add boiled cubed potatoes, turmeric and salt. Toss until golden.",
                ing("potato", 3, "pcs"), ing("cumin", 1, "tsp"), ing("oil", 2, "tbsp"),
                ing("turmeric", 0.5, "tsp"), ing("salt", 1, "tsp")));

        repo.save(recipe("Dal Tadka",
                "Pressure cook dal with turmeric. Make a tadka of cumin, garlic, onion and tomato, and pour over the dal.",
                ing("toor dal", 1, "cup"), ing("onion", 1, "pcs"), ing("tomato", 2, "pcs"),
                ing("garlic", 4, "pcs"), ing("cumin", 1, "tsp"), ing("turmeric", 0.5, "tsp"), ing("salt", 1, "tsp")));

        repo.save(recipe("Paneer Bhurji",
                "Saute onion, tomato and green chilli with turmeric. Crumble in paneer and cook for 5 minutes.",
                ing("paneer", 200, "g"), ing("onion", 1, "pcs"), ing("tomato", 1, "pcs"),
                ing("green chilli", 1, "pcs"), ing("turmeric", 0.5, "tsp"), ing("salt", 1, "tsp")));
    }
}