package kr.hhplus.ecommerce.domain.product;

public class ProductEvent {

    // 재고 차감 이벤트
    public record StockDeducted(
            Long orderId,
            Long productOptionId,
            Long quantity,
            Long remainingStock
    ) {}

    // 재고 부족 이벤트
    public record StockInsufficient(
            Long orderId,
            Long productOptionId,
            Long requestedQuantity,
            Long currentStock
    ) {}
}
