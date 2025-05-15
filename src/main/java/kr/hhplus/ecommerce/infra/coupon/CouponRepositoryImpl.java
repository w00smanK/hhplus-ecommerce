package kr.hhplus.ecommerce.infra.coupon;

import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Optional<Coupon> findByIdWithLock(Long couponId) {
        return couponJpaRepository.findByIdWithLock(couponId);
    }

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return Optional.ofNullable(couponJpaRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다.")));
    }
}
