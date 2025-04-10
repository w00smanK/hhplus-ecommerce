package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.coupon.dto.CouponInfo;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public void publishCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        coupon.validatePublishable();
    }

    public CouponInfo.Coupon getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        return CouponInfo.Coupon.builder()
                .couponId(coupon.getId())
                .discountRate(coupon.getDiscountRate())
                .build();
    }
}
