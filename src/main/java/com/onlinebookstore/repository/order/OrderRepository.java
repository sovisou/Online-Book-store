package com.onlinebookstore.repository.order;

import com.onlinebookstore.model.Order;
import com.onlinebookstore.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);
}
