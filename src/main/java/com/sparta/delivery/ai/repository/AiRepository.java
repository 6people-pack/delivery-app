package com.sparta.delivery.ai.repository;

import com.sparta.delivery.ai.domain.Ai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AiRepository extends JpaRepository<Ai, UUID> , QuerydslPredicateExecutor<Ai> {

    List<Ai> findAllByOrderByCreatedAtDesc();
    List<Ai> findAllByCreatedAtAfter(LocalDateTime createdAt);
    List<Ai> findAllByCreatedAtBefore(LocalDateTime endDay);
    List<Ai> findAllByCreatedAtAfterAndCreatedAtBefore(LocalDateTime startDay, LocalDateTime endDay);
    List<Ai> findAllByAnswerContaining(String word);
    List<Ai> findAllByAnswerContainingAndCreatedAtAfter(String word, LocalDateTime createdAt);
    List<Ai> findAllByAnswerContainingAndCreatedAtBefore(String word, LocalDateTime endDay);
    List<Ai> findAllByAnswerContainingAndCreatedAtAfterAndCreatedAtBefore(String word, LocalDateTime startDay, LocalDateTime endDay);


}
