package kr.hhplus.ecommerce.domain.coupon.repository;

import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CouponRepository {
    Optional<Coupon> findById(Long couponId);
}
