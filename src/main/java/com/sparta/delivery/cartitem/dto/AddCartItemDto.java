package com.sparta.delivery.cartitem.dto;

import com.sparta.delivery.cartitem.domain.TempMenu;

public record AddCartItemDto(

        TempMenu menu,

        String option,

        int quantity

) {}
