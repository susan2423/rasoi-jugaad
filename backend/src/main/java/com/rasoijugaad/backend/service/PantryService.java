package com.rasoijugaad.backend.service;

import com.rasoijugaad.backend.model.PantryItem;
import com.rasoijugaad.backend.repository.PantryItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class PantryService {
    private final PantryItemRepository repo;

    public PantryService(PantryItemRepository repo) {
        this.repo = repo;
    }

    public List<PantryItem> findAll() {
        return repo.findAll(Sort.by("expiryDate"));
    }

    public PantryItem save(PantryItem item) {
        return repo.save(item);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    // Smart Alerts: anything expiring within 2 days (or already expired)
    public List<PantryItem> expiringSoon() {
        return repo.findByExpiryDateLessThanEqualOrderByExpiryDateAsc(LocalDate.now().plusDays(2));
    }
}