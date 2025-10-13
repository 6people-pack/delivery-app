package com.sparta.delivery.cartitem.dto;

import java.util.UUID;

public record updateCartItemQuantityDto(
        UUID cartItemId,
        boolean increase

) {}
