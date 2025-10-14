package com.sparta.delivery.order.dto;

import java.util.UUID;

public record CancelOrderOwnerDto(
        UUID orderId,
        String cancelMessage

) {}
