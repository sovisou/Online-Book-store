package com.onlinebookstore.service.impl;

import com.onlinebookstore.dto.cart.CartItemUpdateDto;
import com.onlinebookstore.dto.cart.CreateCartItemRequestDto;
import com.onlinebookstore.dto.cart.ShoppingCartDto;
import com.onlinebookstore.exception.EntityNotFoundException;
import com.onlinebookstore.mapper.CartItemMapper;
import com.onlinebookstore.mapper.ShoppingCartMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.CartItem;
import com.onlinebookstore.model.ShoppingCart;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.cart.CartItemRepository;
import com.onlinebookstore.repository.cart.ShoppingCartRepository;
import com.onlinebookstore.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public void createShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto getShoppingCartByUserId(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't get shopping cart for user with id: " + userId));
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addCartItem(Long userId, CreateCartItemRequestDto requestDto) {
        Long book = requestDto.getBookId();
        Book bookById = bookRepository.findById(book).orElseThrow(
                () -> new EntityNotFoundException("Can't get book by id: " + book));
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't get shopping cart for user with id: " + userId));
        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItem.setBook(bookById);
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
        return getShoppingCartByUserId(userId);
    }

    @Override
    public ShoppingCartDto updateCartItem(Long cartItemId, CartItemUpdateDto updateDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                "Can't find cart with id: " + cartItemId));
        cartItemMapper.updateItemFromDto(updateDto, cartItem);
        cartItemRepository.save(cartItem);
        ShoppingCart shoppingCart = cartItem.getShoppingCart();
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
