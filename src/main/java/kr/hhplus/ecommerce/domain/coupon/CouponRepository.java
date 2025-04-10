package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository {

    Coupon findById(Long couponId);
}
