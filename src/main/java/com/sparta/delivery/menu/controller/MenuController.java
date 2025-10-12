package com.sparta.delivery.menu.controller;

import com.sparta.delivery.menu.dto.MenuCreateRequest;
import com.sparta.delivery.menu.dto.MenuResponse;
import com.sparta.delivery.menu.dto.MenuUpdateRequest;
import com.sparta.delivery.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {

    private final MenuService menuService;

    // POST /api/menus
    @PostMapping("/menus")
    public ResponseEntity<MenuResponse> create(@RequestBody @Valid MenuCreateRequest req) {
        MenuResponse created = menuService.create(req);
        return ResponseEntity.created(URI.create("/api/menus/" + created.id())).body(created);
    }

    // PATCH /api/menus/{menuId}
    @PatchMapping("/menus/{menuId}")
    public ResponseEntity<MenuResponse> update(@PathVariable UUID menuId,
                                               @RequestBody @Valid MenuUpdateRequest req) {
        return ResponseEntity.ok(menuService.update(menuId, req));
    }

    // GET /api/menus/{menuId}
    @GetMapping("/menus/{menuId}")
    public ResponseEntity<MenuResponse> get(@PathVariable UUID menuId) {
        return ResponseEntity.ok(menuService.get(menuId));
    }

    // GET /api/restaurants/{restaurantId}/menus
    @GetMapping("/restaurants/{restaurantId}/menus")
    public ResponseEntity<List<MenuResponse>> listByRestaurant(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(menuService.listByRestaurant(restaurantId));
    }

    // DELETE /api/menus/{menuId}
    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<Void> delete(@PathVariable UUID menuId) {
        menuService.delete(menuId);
        return ResponseEntity.noContent().build();
    }
}