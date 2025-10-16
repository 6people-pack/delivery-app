package com.sparta.delivery.cartitem.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddCartItemRequestDto(

        @NotNull(message = "메뉴를 입력해주세요.")
        UUID menuId,

        @NotNull(message = "식당를 입력해주세요.")
        UUID restaurantId,

        String option,

        @NotNull(message = "수량을 입력해주세요.")
        int quantity,

        // 다른 식당의 메뉴를 추가할 때 덮어 쓰시겠습니까 메세지에 확인, 취소에 따라 추가로 보내서 덮어쓸지 여부 결정
        Boolean override
) {}
