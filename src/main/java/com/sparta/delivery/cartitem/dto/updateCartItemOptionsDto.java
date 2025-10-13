package com.sparta.delivery.cartitem.dto;

import java.util.UUID;

public record updateCartItemOptionsDto(

        UUID cartItemId,

        String options
) {}
