package kr.hhplus.ecommerce.application.order.dto;

import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;

public record OrderResult() {

    public record Create(
            Long orderId,
            Long userId,
            OrderStatus status,
            Long totalAmount,
            Long discountAmount,
            Long paymentAmount
    ) {
        public static Create from(OrderInfo.Create orderInfo) {
            return new Create(
                    orderInfo.orderId(),
                    orderInfo.userId(),
                    orderInfo.status(),
                    orderInfo.totalAmount(),
                    orderInfo.discountAmount(),
                    orderInfo.paymentAmount()
            );
        }
    }

}
