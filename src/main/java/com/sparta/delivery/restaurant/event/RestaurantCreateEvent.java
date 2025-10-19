package com.sparta.delivery.restaurant.event;

import com.sparta.delivery.restaurant.domain.Restaurant;
import com.sparta.delivery.user.domain.User;
import lombok.Getter;

@Getter
public class RestaurantCreateEvent  {
    private final User user;
    private final Restaurant restaurant;

    public RestaurantCreateEvent(Restaurant restaurant, User user) {
        this.restaurant = restaurant;
        this.user = user;
    }
}
