package kr.hhplus.ecommerce.interfaces.scheduler;

import kr.hhplus.ecommerce.application.rank.RankFacade;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 쿠폰 일일 스케줄러
 * 매일 0시 00분에 실행되어 100개의 쿠폰을 발급
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyScheduler {

    private final CouponService couponService;
    private final RankFacade rankFacade;

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
            couponService.initializeFirstComeCoupon();
            log.info("===== 일일 쿠폰 발급 완료 ({}개) =====", COUPON_QUANTITY);
        } catch (Exception e) {
            log.error("일일 쿠폰 발급 중 오류 발생", e);
        }
    }

    /**
     * 매일 0시 05분에 실행되는 판매 순위 갱신 스케줄러
     * 전날의 판매 데이터를 기반으로 랭킹을 갱신
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void updateDailyRank() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("일별 판매 순위 자동 갱신 시작 - 날짜: {}", yesterday);
        rankFacade.createDailyRankAt(yesterday);
    }
}