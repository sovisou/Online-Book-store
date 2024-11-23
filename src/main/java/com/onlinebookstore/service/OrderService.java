package com.onlinebookstore.service;

import com.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderItemDto;
import com.onlinebookstore.dto.order.OrderUpdateDto;
import java.util.List;

public interface OrderService {
    OrderDto createOrder(Long userId, CreateOrderRequestDto requestDto);

    List<OrderDto> findAllOrders(Long userId);

    OrderDto updateOrderStatus(Long orderId, OrderUpdateDto updateDto);

    List<OrderItemDto> findAllItems(Long orderId);

    OrderItemDto getItemFromOrderId(Long orderId, Long orderItemId);
}
