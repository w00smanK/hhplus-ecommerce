package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCouponStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssuedCouponRepository {

    IssuedCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    IssuedCoupon findById(Long issuedCouponId);

    List<IssuedCoupon> findByUserIdAndStatus(Long userId, IssuedCouponStatus statuses);

}
