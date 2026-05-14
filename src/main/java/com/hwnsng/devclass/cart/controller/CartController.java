package com.hwnsng.devclass.cart.controller;

import com.hwnsng.devclass.cart.dto.AddCartRequest;
import com.hwnsng.devclass.cart.dto.CartItemResponse;
import com.hwnsng.devclass.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cart", description = "장바구니 API")
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 조회")
    @GetMapping("/api/users/{userId}/cart")
    public ResponseEntity<List<CartItemResponse>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @Operation(summary = "장바구니 담기")
    @PostMapping("/api/users/{userId}/cart")
    public ResponseEntity<CartItemResponse> addToCart(
            @PathVariable Long userId,
            @RequestBody AddCartRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addToCart(userId, req.getCourseId()));
    }

    @Operation(summary = "장바구니 항목 삭제")
    @DeleteMapping("/api/cart/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long cartItemId,
            @RequestParam Long userId) {
        cartService.removeFromCart(cartItemId, userId);
        return ResponseEntity.noContent().build();
    }
}
