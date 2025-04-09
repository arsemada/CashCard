package com.example.CashCard.repository;

import com.example.CashCard.entity.CashCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashCardRepository extends JpaRepository<CashCard, Long> {
    // You can add custom query methods here later
}
