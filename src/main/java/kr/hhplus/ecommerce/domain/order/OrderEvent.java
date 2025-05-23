package kr.hhplus.ecommerce.domain.order;

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

    public static OrderEvent from(Order info) {
        return OrderEvent.builder()
                .orderId(info.getId())
                .userId(info.getUserId())
                .totalAmount(info.getTotalAmount())
                .discountAmount(info.getDiscountAmount())
                .paymentAmount(info.getPaymentAmount())
                .orderedAt(LocalDateTime.now())
                .build();
    }
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
    ) {}
}
