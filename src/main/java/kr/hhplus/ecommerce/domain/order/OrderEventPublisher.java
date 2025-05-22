package kr.hhplus.ecommerce.domain.order;

public interface OrderEventPublisher {

    void complete(OrderCompleteEvent event);
}
