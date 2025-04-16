package kr.hhplus.ecommerce.domain.order.repository;

import kr.hhplus.ecommerce.domain.order.entity.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrderItemRepository {

    Optional<OrderItem> findByProductOptionId(Long command);

    OrderItem save(OrderItem orderItem);

}
