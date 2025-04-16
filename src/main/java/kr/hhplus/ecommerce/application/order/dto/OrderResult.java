package kr.hhplus.ecommerce.application.order.dto;

import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import lombok.Builder;

public record OrderResult() {

    @Builder
    public record Create(
            Long orderId,
            Long userId,
            OrderStatus status,
            Long totalAmount,
            Long discountAmount,
            Long paymentAmount
    ) {
        public static Create from(OrderInfo.Create orderInfo) {
            return Create.builder()
                    .orderId(orderInfo.orderId())
                    .userId(orderInfo.userId())
                    .status(orderInfo.status())
                    .totalAmount(orderInfo.totalAmount())
                    .discountAmount(orderInfo.discountAmount())
                    .paymentAmount(orderInfo.paymentAmount())
                    .build();
        }
    }
}
