package com.example.CashCard.repository;

import com.example.CashCard.entity.CashCard;

import java.util.List;
import java.util.Optional;

public class CashCardRepository {
    public CashCard save(CashCard newCashCard) {
        return newCashCard;
    }

    public Optional<CashCard> findById(Long id) {
        return Optional.empty();
    }

    public List<CashCard> findAll() {
        return List.of();
    }

    public boolean existsById(Long id) {
        return false;
    }

    public void deleteById(Long id) {
    }
}
