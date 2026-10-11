package com.rasoijugaad.backend.controller;

import com.rasoijugaad.backend.dto.RecipeMatch;
import com.rasoijugaad.backend.model.Recipe;
import com.rasoijugaad.backend.repository.RecipeRepository;
import com.rasoijugaad.backend.service.MatchService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeRepository recipeRepo;
    private final MatchService matchService;

    public RecipeController(RecipeRepository recipeRepo, MatchService matchService) {
        this.recipeRepo = recipeRepo;
        this.matchService = matchService;
    }

    @GetMapping
    public List<Recipe> all() {
        return recipeRepo.findAll();
    }

    @GetMapping("/match")
    public List<RecipeMatch> match() {
        return matchService.match();
    }
}