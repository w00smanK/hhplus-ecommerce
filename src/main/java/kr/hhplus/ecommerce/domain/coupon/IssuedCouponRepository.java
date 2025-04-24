package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface IssuedCouponRepository {

    Optional<IssuedCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    Optional<IssuedCoupon> findById(Long issuedCouponId);

    IssuedCoupon save(IssuedCoupon issuedCoupon);
}
