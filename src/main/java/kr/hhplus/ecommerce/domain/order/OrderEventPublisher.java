package kr.hhplus.ecommerce.domain.order;

public interface OrderEventPublisher {

    void complete(OrderEvent event);

    void publish(OrderEvent event);
}
