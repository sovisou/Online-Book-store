package com.onlinebookstore.service;

import com.onlinebookstore.dto.cart.CartItemUpdateDto;
import com.onlinebookstore.dto.cart.CreateCartItemRequestDto;
import com.onlinebookstore.dto.cart.ShoppingCartDto;
import com.onlinebookstore.model.ShoppingCart;
import com.onlinebookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartByUserId(Long userId);

    ShoppingCartDto addCartItem(Long userId, CreateCartItemRequestDto requestDto);

    ShoppingCartDto updateCartItem(Long cartItemId, CartItemUpdateDto updateDto);

    void removeCartItem(Long cartItemId);

    ShoppingCart createShoppingCartForUser(User user);

    boolean isCartItemBelongsToUser(Long cartItemId, Long userId);
}
