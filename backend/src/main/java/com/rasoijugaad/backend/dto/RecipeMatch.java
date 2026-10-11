package com.rasoijugaad.backend.dto;

import java.util.List;

public record RecipeMatch(
        Long recipeId,
        String title,
        String instructions,
        int matchPercent,
        List<String> have,
        List<String> missing,
        List<String> usesExpiring) {
}