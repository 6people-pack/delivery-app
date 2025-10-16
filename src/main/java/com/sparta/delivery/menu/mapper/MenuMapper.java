package com.sparta.delivery.menu.mapper;

import com.sparta.delivery.menu.domain.Menu;
import com.sparta.delivery.menu.domain.MenuStatus;
import com.sparta.delivery.menu.dto.MenuCreateRequestDto;
import com.sparta.delivery.menu.dto.MenuResponseDto;
import com.sparta.delivery.menu.dto.MenuSummaryDto;   // ★ 추가
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MenuMapper {

    public Menu toEntity(MenuCreateRequestDto req, UUID restaurantId) {
        return Menu.builder()
                .restaurantId(restaurantId)
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .discountPrice(req.discountPrice() != null ? req.discountPrice() : 0)
                .option(req.option())
                .status(req.status() != null ? req.status() : MenuStatus.SALE)
                .build();
    }

    public MenuResponseDto toResponse(Menu m) {
        return new MenuResponseDto(
                m.getId(),
                m.getRestaurantId(),
                m.getName(),
                m.getDescription(),
                m.getPrice(),
                m.getDiscountPrice(),
                m.getOption(),
                m.getStatus(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }

    //목록 전용 요약 매핑
    public MenuSummaryDto toSummary(Menu m) {
        return new MenuSummaryDto(
                m.getId(),
                m.getName(),
                m.getPrice(),
                m.getDiscountPrice(),
                m.getStatus()
        );
    }
}