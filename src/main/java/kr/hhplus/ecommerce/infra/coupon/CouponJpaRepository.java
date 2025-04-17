package kr.hhplus.ecommerce.infra.coupon;


import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
