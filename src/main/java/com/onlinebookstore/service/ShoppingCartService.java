package com.onlinebookstore.service;

import com.onlinebookstore.dto.cart.CartItemDto;
import com.onlinebookstore.dto.cart.CartItemUpdateDto;
import com.onlinebookstore.dto.cart.CreateCartItemRequestDto;
import com.onlinebookstore.dto.cart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartByUserId(Long userId);

    ShoppingCartDto addCartItem(Long userId, CreateCartItemRequestDto requestDto);

    CartItemDto updateCartItem(Long cartItemId, CartItemUpdateDto updateDto);

    void removeCartItem(Long cartItemId);
}
