package com.sparta.delivery.discord.message;

import java.util.Map;

public interface DiscordEmbeddable {

    String getTitle();
    Map<String, String> getFields();

}
