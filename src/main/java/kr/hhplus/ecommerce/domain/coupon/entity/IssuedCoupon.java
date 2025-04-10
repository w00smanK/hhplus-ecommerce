package kr.hhplus.ecommerce.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssuedCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issued_id")
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    private IssuedCouponStatus status;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Builder
    public IssuedCoupon(Long userId, Coupon coupon, IssuedCouponStatus status, LocalDateTime issuedAt, LocalDateTime expiredAt) {
        this.userId = userId;
        this.coupon = coupon;
        this.status = status;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
    }

    public void use() {
        if (!isUsable()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }
        this.status = IssuedCouponStatus.USED;
    }
    public boolean isExpired() {
        return this.expiredAt.isBefore(LocalDateTime.now()) || this.status == IssuedCouponStatus.EXPIRED;
    }

    public void markAsExpired() {
        this.status = IssuedCouponStatus.EXPIRED;
    }

    public boolean isUsable() {
        return this.status == IssuedCouponStatus.ACTIVE && !isExpired();
    }
}