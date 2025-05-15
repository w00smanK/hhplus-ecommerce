package kr.hhplus.ecommerce.scheduler;

import kr.hhplus.ecommerce.application.coupon.CouponFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 쿠폰 일일 스케줄러
 * 매일 0시 00분에 실행되어 100개의 쿠폰을 발급
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponDailyScheduler {

    private final CouponFacade couponFacade;

    // 쿠폰 수량
    private static final Integer COUPON_QUANTITY = 100;

    /**
     * 매일 0시 00분에 실행되는 쿠폰 발급 스케줄러
     * 100개의 쿠폰을 발급
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void publishDailyCoupon() {
        log.info("===== 일일 쿠폰 발급 스케줄러 실행 =====");
        try {
            // 100개의 쿠폰 발급
            couponFacade.initializeFirstComeCoupon();
            log.info("===== 일일 쿠폰 발급 완료 ({}개) =====", COUPON_QUANTITY);
        } catch (Exception e) {
            log.error("일일 쿠폰 발급 중 오류 발생", e);
        }
    }
}