package kr.hhplus.ecommerce.domain.order;


import kr.hhplus.ecommerce.domain.order.entity.Order;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long orderId);
}
