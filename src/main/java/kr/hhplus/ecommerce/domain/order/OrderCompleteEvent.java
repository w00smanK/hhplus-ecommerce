package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCompleteEvent {

    private Long orderId;
    private Long userId;
    private Long totalAmount;
    private Long discountAmount;
    private Long paymentAmount;
    private LocalDateTime orderedAt;

    public static OrderCompleteEvent from(OrderInfo.Create info) {
        return OrderCompleteEvent.builder()
                .orderId(info.orderId())
                .userId(info.userId())
                .totalAmount(info.totalAmount())
                .discountAmount(info.discountAmount())
                .paymentAmount(info.paymentAmount())
                .orderedAt(LocalDateTime.now())
                .build();
    }

    @Builder
    private OrderCompleteEvent(Long orderId, Long userId, Long totalAmount, Long discountAmount, Long paymentAmount, LocalDateTime orderedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.paymentAmount = paymentAmount;
        this.orderedAt = orderedAt;
    }
}
