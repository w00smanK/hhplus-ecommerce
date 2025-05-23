package kr.hhplus.ecommerce.domain.order;

public interface OrderEventPublisher {

    void complete(OrderEvent.OrderComplete event);

    void publish(OrderEvent.OrderCreated event);

    void payOrder(OrderEvent.OrderConfirmed event);
}
