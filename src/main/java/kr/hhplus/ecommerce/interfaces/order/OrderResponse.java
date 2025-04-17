package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.application.order.dto.OrderResult;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;

public record OrderResponse(
) {

    public record Create(
            Long orderId,
            Long userId,
            OrderStatus status,
            Long totalAmount,
            Long discountAmount,
            Long paymentAmount
    ) {
        public static Create from(OrderResult.Create result) {
            return new Create(
                    result.orderId(),
                    result.userId(),
                    result.status(),
                    result.totalAmount(),
                    result.discountAmount(),
                    result.paymentAmount()
            );
        }
    }
}
