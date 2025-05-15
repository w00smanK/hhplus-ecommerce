package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findByIdWithLock(Long id);

    Optional<Coupon> findById(Long couponId);

    Coupon save(Coupon coupon);


}
