package com.onlinebookstore.service;

import com.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderItemDto;
import com.onlinebookstore.dto.order.OrderUpdateDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(Long userId, CreateOrderRequestDto requestDto);

    Page<OrderDto> findAllOrders(Long userId, Pageable pageable);

    OrderDto updateOrderStatus(Long orderId, OrderUpdateDto updateDto);

    List<OrderItemDto> findAllItems(Long orderId);

    OrderItemDto getItemFromOrderId(Long orderId, Long orderItemId);
}
