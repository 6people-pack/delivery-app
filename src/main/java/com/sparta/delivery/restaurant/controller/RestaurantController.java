package com.sparta.delivery.restaurant.controller;

import com.sparta.delivery.global.unit.common.BaseResponse;
import com.sparta.delivery.global.unit.common.BaseStatus;
import com.sparta.delivery.restaurant.dto.RatingRequestDto;
import com.sparta.delivery.restaurant.dto.RestaurantRequestDto;
import com.sparta.delivery.restaurant.service.RestaurantService;
import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 식당 등록
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/restaurants")
    public BaseResponse<?> createRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestPart(value = "restaurantImage", required = false) List<MultipartFile> restaurantImage,
                                            @Valid @RequestPart(value = "restaurantInfo") RestaurantRequestDto requestDto) {
        restaurantService.createRestaurant(userDetails.getUser(), requestDto, restaurantImage);
        return BaseResponse.ok(BaseStatus.CREATED);
    }

    // 식당 수정
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/restaurants/{restaurantId}")
    public BaseResponse<?> editRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestPart(value = "restaurantImage", required = false) List<MultipartFile> restaurantImage,
                                          @Valid @RequestPart(value = "restaurantInfo") RestaurantRequestDto requestDto,
                                          @PathVariable UUID restaurantId) {
        restaurantService.editRestaurant(userDetails.getUser(), requestDto, restaurantId, restaurantImage);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 식당 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/restaurants/{restaurantId}")
    public BaseResponse<?> deleteRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable UUID restaurantId) {
        restaurantService.deleteRestaurant(userDetails.getUser(), restaurantId);
        return BaseResponse.ok(BaseStatus.OK);
    }

    // 식당 조회 (사장)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/restaurants")
    public BaseResponse<?> getRestaurants(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return BaseResponse.ok(restaurantService.getRestaurants(userDetails.getUser()), BaseStatus.OK);
    }

    // 식당 리스트 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/restaurants/list")
    public BaseResponse<?> getAllRestaurants(@RequestParam(defaultValue = "0", required = false) int page,
                                             @RequestParam(defaultValue = "20", required = false) int size,
                                             @RequestParam(defaultValue = "rating", required = false) String sortBy,
                                             @RequestParam(value = "category", required = false) UUID category,
                                             @RequestParam(value = "name", required = false) String name,
                                             @RequestParam(value = "lat") Double lat,
                                             @RequestParam(value = "lon") Double lon) {
        return BaseResponse.ok(restaurantService.getAllRestaurants(page, size, sortBy, category, name, lat, lon), BaseStatus.OK);
    }

    // 식당 상세 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/restaurants/{restaurantId}")
    public BaseResponse<?> getRestaurantDetail(@PathVariable UUID restaurantId) {
        return BaseResponse.ok(restaurantService.getRestaurantDetail(restaurantId), BaseStatus.OK);
    }

    // 식당 별점 갱신
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/restaurants/{restaurantId}/rating")
    public BaseResponse<?> editRating(@PathVariable UUID restaurantId,
                                      @RequestBody @Valid RatingRequestDto requestDto) {
        restaurantService.editRating(restaurantId, requestDto);
        return BaseResponse.ok(BaseStatus.OK);
    }
}
