package kr.hhplus.ecommerce.domain.product;

public interface ProductEventPublisher {
    void publish(ProductEvent.StockDeducted event);
    void publish(ProductEvent.StockInsufficient event);
}
