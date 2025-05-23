package kr.hhplus.ecommerce.domain.order.dto;


import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;

import java.time.LocalDate;
import java.util.List;

public record OrderCommand() {

    public record Create(
            Long userId,
            List<OrderItem> orderItems
    ) {
    }

    public record OrderItem(
            Long productOptionId,
            Long price,
            Long quantity
    ) {
    }
    public record OrderItemList (
            List<OrderItem> orderItems
    ) {
        public static OrderItemList toCommand(List<OrderItem> orderItems) {
            return new OrderItemList(orderItems);
        }
    }

    public record HoldOrder(
            Long orderId,
            List<ProductInfo.StockStatus> stockDetails
    ) {
    }

    public record UseCoupon(
            Long orderId,
            Long couponId,
            Long discountPrice
    ) {
        public static UseCoupon toCommand(Long orderId, Long couponId, Long discountPrice) {
            return new UseCoupon(orderId, couponId, discountPrice);
        }
    }

    public record Find(
            Long orderId
    ) {
    }

    public record Send(
            Long id,
            Long userId,
            Long issuedCouponId,
            OrderStatus status,
            Long paymentAmount,
            Long totalAmount,
            Long discountAmount
    ) {
        public static Send of(OrderEvent.OrderComplete event) {
            return new Send(
                    event.orderId(),
                    event.userId(),
                    event.issuedCouponId(),
                    event.status(),
                    event.paymentAmount(),
                    event.totalAmount(),
                    event.discountAmount()
            );
        }
    }

    public record FindBest(
            Integer days,
            Integer limit
    ) {
    }

    public record DateQuery(
            LocalDate date
    ) {
        public static DateQuery of(LocalDate date) {
            return new DateQuery(date);
        }
    }
}
