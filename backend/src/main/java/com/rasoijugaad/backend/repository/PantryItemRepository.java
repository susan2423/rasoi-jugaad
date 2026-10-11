package com.rasoijugaad.backend.repository;

import com.rasoijugaad.backend.model.PantryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PantryItemRepository extends JpaRepository<PantryItem, Long> {
    List<PantryItem> findByExpiryDateLessThanEqualOrderByExpiryDateAsc(LocalDate date);
}