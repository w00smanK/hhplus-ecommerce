package kr.hhplus.ecommerce.infra.order;

import kr.hhplus.ecommerce.domain.order.OrderRepository;
import kr.hhplus.ecommerce.domain.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return jpaRepository.findById(orderId);
    }
}
