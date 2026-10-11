package com.rasoijugaad.backend.controller;

import com.rasoijugaad.backend.model.PantryItem;
import com.rasoijugaad.backend.service.PantryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pantry")
public class PantryController {
    private final PantryService service;

    public PantryController(PantryService service) {
        this.service = service;
    }

    @GetMapping
    public List<PantryItem> all() {
        return service.findAll();
    }

    @GetMapping("/expiring")
    public List<PantryItem> expiring() {
        return service.expiringSoon();
    }

    @PostMapping
    public PantryItem add(@Valid @RequestBody PantryItem item) {
        item.setId(null);
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}