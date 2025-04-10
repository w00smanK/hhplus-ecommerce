package kr.hhplus.ecommerce.infra.order;

import kr.hhplus.ecommerce.domain.order.entity.Order;
import kr.hhplus.ecommerce.domain.order.entity.OrderProduct;
import kr.hhplus.ecommerce.domain.order.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public Order save(Order order) {
        return null;
    }

    @Override
    public Order findById(Long orderId) {
        return null;
    }

    @Override
    public void sendOrderMessage(Order order) {

    }

    @Override
    public List<OrderProduct> findOrderIdsIn(List<Long> orderIds) {
        return null;
    }

}
