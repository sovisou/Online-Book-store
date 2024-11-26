package com.onlinebookstore.controller;

import com.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderItemDto;
import com.onlinebookstore.dto.order.OrderUpdateDto;
import com.onlinebookstore.model.User;
import com.onlinebookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place an order")
    public OrderDto createOrder(Authentication authentication,
                                @RequestBody @Valid CreateOrderRequestDto requestDto) {
        Long userId = extractUserId(authentication);
        return orderService.createOrder(userId, requestDto);
    }

    @GetMapping
    @Operation(summary = "Retrieve user's order history")
    public List<OrderDto> getAllOrders(Authentication authentication, Pageable pageable) {
        Long userId = extractUserId(authentication);
        return orderService.findAllOrders(userId, pageable);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update order status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public OrderDto updateOrderStatus(@PathVariable Long id,
                                      @RequestBody @Valid OrderUpdateDto updateDto) {
        return orderService.updateOrderStatus(id, updateDto);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Retrieve all OrderItems for a specific order")
    public List<OrderItemDto> getAllOrderItemsByOrder(@PathVariable Long orderId) {
        return orderService.findAllItems(orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Retrieve a specific OrderItem within an order")
    public OrderItemDto getItemByOrder(@PathVariable Long orderId,
                                       @PathVariable Long itemId) {
        return orderService.getItemFromOrderId(orderId, itemId);
    }

    private Long extractUserId(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}
