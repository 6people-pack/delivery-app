package com.sparta.delivery.ai.repository;

import com.sparta.delivery.ai.domain.Ai;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiRepository extends JpaRepository<Ai, UUID> {
}
