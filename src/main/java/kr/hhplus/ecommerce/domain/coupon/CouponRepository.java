package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public interface CouponRepository {
    Optional<Coupon> findById(Long couponId);

    Coupon save(Coupon coupon);

}
