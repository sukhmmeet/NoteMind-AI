package com.dhaliwal.notemind.repository;

import com.dhaliwal.notemind.entity.FlashCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashCardRepository extends JpaRepository<FlashCard, Long> {

}