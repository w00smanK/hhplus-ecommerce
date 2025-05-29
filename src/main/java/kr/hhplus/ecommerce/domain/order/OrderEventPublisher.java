package kr.hhplus.ecommerce.domain.order;

public interface OrderEventPublisher {

    void orderComplete(OrderEvent.OrderComplete event);

    void orderPublish(OrderEvent.OrderCreated event);
}
