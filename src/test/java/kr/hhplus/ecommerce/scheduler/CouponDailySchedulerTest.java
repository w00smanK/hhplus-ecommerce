package kr.hhplus.ecommerce.scheduler;

import kr.hhplus.ecommerce.application.coupon.CouponFacade;
import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.domain.coupon.CouponRedisRepository;
import kr.hhplus.ecommerce.interfaces.scheduler.CouponDailyScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("[통합테스트] CouponDailyScheduler")
@ActiveProfiles("test")
class CouponDailySchedulerTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponRedisRepository couponRedisRepository;

    @Autowired
    private CouponDailyScheduler couponDailyScheduler;

    @Test
    @DisplayName("일일 쿠폰 발급 스케줄러 테스트")
    void couponDailySchedulerTest() {
        // 스케줄러의 publishDailyCoupon 메서드 직접 호출
        couponDailyScheduler.publishDailyCoupon();

        // 쿠폰이 생성되었는지 확인
        Coupon coupon = couponRepository.findById(1L)
                .orElseGet(() -> {
                    // 쿠폰 ID가 1이 아닌 경우 가장 최근에 생성된 쿠폰을 찾아야 할 수 있음
                    // 실제 환경에서는 쿠폰 ID를 알 수 있는 방법이 필요함
                    return null;
                });

        // 쿠폰이 생성되었는지 확인
        assertThat(coupon).isNotNull();

        // 쿠폰 수량이 100개인지 확인
        assertThat(coupon.getQuantity()).isEqualTo(100);

        // Redis에 쿠폰이 초기화되었는지 확인
        long redisStock = couponRedisRepository.getCouponStock(coupon.getId());
        assertThat(redisStock).isEqualTo(100);
    }
}
