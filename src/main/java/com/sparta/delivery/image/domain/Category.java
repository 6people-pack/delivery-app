package com.sparta.delivery.image.domain;

import lombok.Getter;

@Getter
public enum Category {
    restaurant,menu,review;

    public static boolean isPresent(String value) {
        for (Category category : Category.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
