package com.sparta.delivery.order.dto;

import java.util.UUID;

public record CancelOrderOwnerRequestDto(
        UUID orderId,
        String cancelMessage

) {}
