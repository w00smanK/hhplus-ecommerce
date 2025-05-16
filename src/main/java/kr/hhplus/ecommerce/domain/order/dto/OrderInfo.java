package kr.hhplus.ecommerce.domain.order.dto;


import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;

import java.util.List;

public record OrderInfo() {

    public record Create(
            Long orderId,
            Long userId,
            Long issuedCouponId,
            OrderStatus status,
            Long totalAmount,
            Long discountAmount,
            Long paymentAmount
    ) {
    }

    public record Best(
            Long productOptionId,
            Long totalSaleQuantity
    ) {
    }

    public record PaidProduct(
            Long productId,
            Long quantity
    ) {
        public static PaidProduct of(Long productId, Long quantity) {
            return new PaidProduct(productId, quantity);
        }
    }

    public record PaidProducts(
            List<PaidProduct> products
    ) {
        public List<PaidProduct> getProducts() {
            return products;
        }

        public static PaidProducts of(List<PaidProduct> products) {
            return new PaidProducts(products);
        }
    }
}
