package kr.hhplus.ecommerce.infra.coupon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CouponManualTest {

    @Autowired
    private CouponApplyRepositoryImpl couponApplyRepository;

    @Test
    void 수동_쿠폰_재고_설정() {
        // Given
        Long couponId = 1L;
        int quantity = 100;

        // When
        couponApplyRepository.setManualCouponStock(couponId, quantity);

        // Then
        long stock = couponApplyRepository.getCouponStock(couponId);
        System.out.println("설정된 쿠폰 재고: " + stock);
    }

    @Test
    void 쿠폰_데이터_초기화() {
        // Given
        Long couponId = 1L;

        // When
        couponApplyRepository.clearAllCouponData(couponId);

        // Then
        System.out.println("초기화 후 재고: " + couponApplyRepository.getCouponStock(couponId));
    }
}
