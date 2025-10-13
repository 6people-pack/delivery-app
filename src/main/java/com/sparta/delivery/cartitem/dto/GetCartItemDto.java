package com.sparta.delivery.cartitem.dto;

import com.sparta.delivery.cartitem.domain.TempMenu;

public record GetCartItemDto(
        TempMenu menu,
        String options,
        int quantity
) {}
