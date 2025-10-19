package com.sparta.delivery.restaurant.dto;

import com.sparta.delivery.restaurant.domain.ApprovalStatus;
import jakarta.validation.constraints.NotNull;

public record RestaurantApproveRequestDto(
        @NotNull(message = "상태 정보는 필수 입력값 입니다.")
        ApprovalStatus approvalStatus
) {
}
