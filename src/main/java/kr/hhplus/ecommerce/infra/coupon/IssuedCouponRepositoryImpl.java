package kr.hhplus.ecommerce.infra.coupon;

import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {

    private final IssuedCouponJpaRepository issuedCouponJpaRepository;

    @Override
    public Optional<IssuedCoupon> findByUserIdAndCouponId(Long userId, Long couponId) {
        return issuedCouponJpaRepository.findByUserIdAndCouponId(userId, couponId);
    }

    @Override
    public Optional<IssuedCoupon> findById(Long issuedCouponId) {
        return issuedCouponJpaRepository.findById(issuedCouponId);
    }

    @Override
    public IssuedCoupon save(IssuedCoupon issuedCoupon) {
        return issuedCouponJpaRepository.save(issuedCoupon);
    }
}
