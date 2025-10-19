package com.sparta.delivery.image.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ImageUpdateRequestDto(@NotBlank String category,
                                    @NotNull UUID categoryId,
                                    List<ImageCDto> create,
                                    List<ImageDDto> delete,
                                    List<ImageUDto> update) {
}

