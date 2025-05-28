package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.order.entity.Order;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEvent {



    public record OrderComplete(
            Long orderId,
            Long userId,
            Long issuedCouponId,
            OrderStatus status,
            Long paymentAmount,
            Long totalAmount,
            Long discountAmount
    ) {
        public static OrderComplete from(Order order) {
            return new OrderComplete(
                order.getId(),
                order.getUserId(),
                order.getIssuedCouponId(),
                order.getStatus(),
                order.getPaymentAmount(),
                order.getTotalAmount(),
                order.getDiscountAmount()
            );
        }
    }

    public record OrderCreated(
            Long orderId,
            Long userId,
            Long couponId,
            List<OrderCommand.OrderItem> orderItems
    ) {}

    public record OrderConfirmed(
            Long orderId,
            Long userId,
            Long paymentAmount
    ) {
        public static OrderConfirmed from(OrderInfo.Create orderInfo, Long userId) {
            return new OrderConfirmed(
                orderInfo.orderId(),
                userId,
                orderInfo.paymentAmount()
            );
        }
    }
}
