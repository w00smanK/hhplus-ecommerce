package kr.hhplus.ecommerce.application.payment.dto;


import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import kr.hhplus.ecommerce.domain.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResult() {

    public record Pay(
            Long paymentId,
            Long orderId,
            Long point,
            OrderStatus orderStatus,
            PaymentStatus paymentStatus,
            Long amount,
            LocalDateTime paidAt
    ) {
    }

}
