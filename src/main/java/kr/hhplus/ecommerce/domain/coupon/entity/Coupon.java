package kr.hhplus.ecommerce.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discount_rate")
    private double discountRate;

    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_status")
    private CouponStatus status;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Builder
    private Coupon(
            Long id,
            double discountRate,
            int quantity,
            CouponStatus status,
            LocalDateTime expiredAt
    ) {
        this.id = id;
        this.discountRate = discountRate;
        this.quantity = quantity;
        this.status = status;
        this.expiredAt = expiredAt;
    }

    public Coupon validatePublishable() {
        if (status.cannotPublishable()) {
            throw new IllegalStateException("쿠폰을 발급할 수 없습니다.");
        }

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("쿠폰이 만료되었습니다.");
        }

        if (quantity <= 0) {
            throw new IllegalStateException("쿠폰 수량이 부족합니다.");
        }

        this.quantity--;
        return this;
    }
    public void decreaseQuantity() {
        this.quantity--;
        if (this.quantity <= 0) {
            this.status = CouponStatus.SOLD_OUT;
        }
    }
}
