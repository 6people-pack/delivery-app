package com.sparta.delivery.menu.dto;

import com.sparta.delivery.menu.domain.MenuStatus;
import java.util.UUID;

public record MenuSummaryDto(
        UUID id,
        String name,
        int price,
        int discountPrice,
        MenuStatus status
) {}