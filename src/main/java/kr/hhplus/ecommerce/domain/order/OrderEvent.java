package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.order.entity.Order;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEvent {

    private Long orderId;
    private Long userId;
    private Long totalAmount;
    private Long discountAmount;
    private Long paymentAmount;
    private LocalDateTime orderedAt;

    @Builder
    public OrderEvent(Long orderId, Long userId, Long totalAmount, Long discountAmount, Long paymentAmount, LocalDateTime orderedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.paymentAmount = paymentAmount;
        this.orderedAt = orderedAt;
    }

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
            Long paymentAmount
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
