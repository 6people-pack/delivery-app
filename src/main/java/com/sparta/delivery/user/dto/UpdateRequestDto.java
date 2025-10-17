package com.sparta.delivery.user.dto;

public record UpdateRequestDto(

        String currentPassword,

        String newPassword,

        String name,

        String nickname,

        String phoneNumber

) {}
