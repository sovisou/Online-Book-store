package com.onlinebookstore.controller;

import com.onlinebookstore.dto.cart.CartItemUpdateDto;
import com.onlinebookstore.dto.cart.CreateCartItemRequestDto;
import com.onlinebookstore.dto.cart.ShoppingCartDto;
import com.onlinebookstore.model.User;
import com.onlinebookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(summary = "Get shopping cart by id",
            description = "Get a shopping cart by its identifier")
    public ShoppingCartDto getShoppingCartByUserId(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return shoppingCartService.getShoppingCartByUserId(userId);
    }

    @PostMapping
    @Operation(summary = "Add item",
            description = "Add item to shopping cart")
    public ShoppingCartDto addItemToCart(Authentication authentication,
                                     @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        Long userId = extractUserId(authentication);
        return shoppingCartService.addCartItem(userId, requestDto);
    }

    @PutMapping("items/{cartItemId}")
    @Operation(summary = "Update category by id",
            description = "Update category info by its identifier")
    public ShoppingCartDto updateCartItemsQuantity(@PathVariable Long cartItemId,
                                      @RequestBody @Valid CartItemUpdateDto updateDto,
                                                   Authentication authentication) {
        Long userId = extractUserId(authentication);
        if (!shoppingCartService.isCartItemBelongsToUser(cartItemId, userId)) {
            throw new AccessDeniedException("Access denied");
        }
        return shoppingCartService.updateCartItem(cartItemId, updateDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("items/{cartItemId}")
    @Operation(summary = "Delete item by id",
            description = "Delete a shopping cart item by its identifier")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        shoppingCartService.removeCartItem(cartItemId);
    }

    private Long extractUserId(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}
