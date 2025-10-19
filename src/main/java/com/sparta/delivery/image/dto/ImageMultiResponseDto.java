package com.sparta.delivery.image.dto;

import java.util.List;
import java.util.UUID;

public record ImageMultiResponseDto(
        String category,
        UUID categoryId,
        List<ImageSimpleResponseDto> images) {
}
