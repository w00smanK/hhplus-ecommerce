package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.coupon.dto.IssuedCouponCommand;
import kr.hhplus.ecommerce.domain.coupon.dto.IssuedCouponInfo;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssuedCouponService {

    private final IssuedCouponRepository issuedCouponRepository;

    public IssuedCouponInfo.UsableCoupon getUsableCoupon(IssuedCouponCommand.UsableCoupon command) {
        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(command.getUserId(), command.getCouponId());

        if (!issuedCoupon.isUsable()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        return IssuedCouponInfo.UsableCoupon.of(issuedCoupon.getId());
    }

    public void useCoupon(Long userCouponId) {
        IssuedCoupon issuedCoupon = issuedCouponRepository.findById(userCouponId);
        issuedCoupon.use();
    }

    public IssuedCouponInfo.Coupons getCoupons(Long userId) {
        List<IssuedCoupon> usedCoupons = issuedCouponRepository.findByUserIdAndStatus(userId, IssuedCouponStatus.USED);

        return IssuedCouponInfo.Coupons.of(
                usedCoupons.stream()
                        .map(issuedCoupon -> IssuedCouponInfo.Coupon.builder()
                                .userCouponId(issuedCoupon.getId())
                                .couponId(issuedCoupon.getCoupon().getId())
                                .issuedAt(issuedCoupon.getIssuedAt())
                                .build())
                        .toList()
        );
    }
}
