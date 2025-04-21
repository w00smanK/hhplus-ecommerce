package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.order.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {

    Optional<OrderItem> findByOrderAndOption(Long orderId, Long productOptionId);

    OrderItem save(OrderItem orderItem);

    List<OrderInfo.Best> findBestSelling(Integer days, Integer limit);

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductOptionId(Long id);
}
