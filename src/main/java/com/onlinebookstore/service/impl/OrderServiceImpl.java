package com.onlinebookstore.service.impl;

import com.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderItemDto;
import com.onlinebookstore.dto.order.OrderUpdateDto;
import com.onlinebookstore.enums.Status;
import com.onlinebookstore.exception.EntityNotFoundException;
import com.onlinebookstore.mapper.OrderItemMapper;
import com.onlinebookstore.mapper.OrderMapper;
import com.onlinebookstore.model.Order;
import com.onlinebookstore.model.OrderItem;
import com.onlinebookstore.model.ShoppingCart;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.cart.ShoppingCartRepository;
import com.onlinebookstore.repository.order.OrderItemRepository;
import com.onlinebookstore.repository.order.OrderRepository;
import com.onlinebookstore.repository.user.UserRepository;
import com.onlinebookstore.service.OrderService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, CreateOrderRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id: " + userId));
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart for user with id: "
                        + userId));
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.getShoppingAddress());
        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
        order.setTotal(orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public List<OrderDto> findAllOrders(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id: " + userId));
        List<Order> orders = orderRepository.findAllByUser(user);
        return orders.stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, OrderUpdateDto updateDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id: " + orderId));
        order.setStatus(updateDto.getStatus());
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderItemDto> findAllItems(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId).stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getItemFromOrderId(Long orderId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findByOrder_IdAndId(orderId, orderItemId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find item with id: "
                        + orderItemId));
        return orderItemMapper.toDto(orderItem);
    }
}
