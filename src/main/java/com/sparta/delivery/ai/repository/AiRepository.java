package com.sparta.delivery.ai.repository;

import com.sparta.delivery.ai.domain.Ai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AiRepository extends JpaRepository<Ai, UUID> , QuerydslPredicateExecutor<Ai> {
}
