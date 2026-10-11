package com.rasoijugaad.backend.service;

import com.rasoijugaad.backend.dto.RecipeMatch;
import com.rasoijugaad.backend.model.PantryItem;
import com.rasoijugaad.backend.model.Recipe;
import com.rasoijugaad.backend.model.RecipeIngredient;
import com.rasoijugaad.backend.repository.PantryItemRepository;
import com.rasoijugaad.backend.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final PantryItemRepository pantryRepo;
    private final RecipeRepository recipeRepo;

    public MatchService(PantryItemRepository pantryRepo, RecipeRepository recipeRepo) {
        this.pantryRepo = pantryRepo;
        this.recipeRepo = recipeRepo;
    }

    // "Tomatoes " -> "tomato", so small spelling differences still match
    static String normalize(String s) {
        String n = s.trim().toLowerCase();
        if (n.endsWith("oes"))
            return n.substring(0, n.length() - 2);
        if (n.endsWith("s") && !n.endsWith("ss"))
            return n.substring(0, n.length() - 1);
        return n;
    }

    @Transactional(readOnly = true)
    public List<RecipeMatch> match() {
        LocalDate today = LocalDate.now();
        LocalDate soon = today.plusDays(2);

        // only usable (not yet expired) items count
        List<PantryItem> usable = pantryRepo.findAll().stream()
                .filter(i -> i.getExpiryDate() == null || !i.getExpiryDate().isBefore(today))
                .toList();

        Set<String> have = usable.stream()
                .map(i -> normalize(i.getName()))
                .collect(Collectors.toSet());

        Set<String> expiring = usable.stream()
                .filter(i -> i.getExpiryDate() != null && !i.getExpiryDate().isAfter(soon))
                .map(i -> normalize(i.getName()))
                .collect(Collectors.toSet());

        List<RecipeMatch> results = new ArrayList<>();
        for (Recipe r : recipeRepo.findAll()) {
            List<String> haveList = new ArrayList<>();
            List<String> missing = new ArrayList<>();
            List<String> usesExpiring = new ArrayList<>();

            for (RecipeIngredient ri : r.getIngredients()) {
                String key = normalize(ri.getIngredientName());
                if (have.contains(key)) {
                    haveList.add(ri.getIngredientName());
                    if (expiring.contains(key))
                        usesExpiring.add(ri.getIngredientName());
                } else {
                    missing.add(ri.getIngredientName());
                }
            }

            int total = r.getIngredients().size();
            int percent = total == 0 ? 0 : haveList.size() * 100 / total;
            results.add(new RecipeMatch(r.getId(), r.getTitle(), r.getInstructions(),
                    percent, haveList, missing, usesExpiring));
        }

        // Rank by match %, with a +15 bonus per expiring ingredient it uses
        results.sort(Comparator
                .comparingInt((RecipeMatch m) -> m.matchPercent() + 15 * m.usesExpiring().size())
                .reversed());
        return results;
    }
}