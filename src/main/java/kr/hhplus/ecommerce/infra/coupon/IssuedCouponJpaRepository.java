package kr.hhplus.ecommerce.infra.coupon;

import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {
    Optional<IssuedCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    List<IssuedCoupon> findByUserId(Long userId);

}
