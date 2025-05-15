package kr.hhplus.ecommerce.domain.coupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.config.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class IssuedCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long couponId;

    private CouponStatus status;

    private LocalDateTime usedAt;

    private LocalDateTime expiredAt;

    public IssuedCoupon(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
        this.status = CouponStatus.ISSUED;
        this.expiredAt = LocalDateTime.now().plusDays(90);
    }

    public void use() {
        if (this.status != CouponStatus.ISSUED) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        this.status = CouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }
}
