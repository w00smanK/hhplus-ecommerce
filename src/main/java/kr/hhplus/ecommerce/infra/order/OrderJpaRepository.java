package kr.hhplus.ecommerce.infra.order;


import kr.hhplus.ecommerce.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
