package kr.hhplus.ecommerce.application.coupon;

import kr.hhplus.ecommerce.application.coupon.dto.CouponCriteria;
import kr.hhplus.ecommerce.concurrency.support.ConcurrentExecutor;
import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.infra.coupon.RedisCouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("[통합테스트] CouponRedisFacade")
@Description("선착순 쿠폰 발급 Redis 테스트")
@ActiveProfiles("test")
@Slf4j
class CouponRedisFacadeTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisCouponRepository redisCouponRepository;

    private Coupon COUPON;

    @BeforeEach
    void setUp() {
        // 테스트용 쿠폰 생성 (10개 수량)
        COUPON = couponRepository.save(new Coupon(1000L, 10));
        
        // Redis에 쿠폰 초기화
        redisCouponRepository.initializeCoupon(COUPON);
    }

    @Test
    @DisplayName("선착순 쿠폰 발급 성공 (Redis Sorted Set 사용)")
    void couponFirstIssueWithRedis_success() throws InterruptedException {
        // Arrange
        int threadCount = 15;  // 15개의 스레드로 동시에 요청 (쿠폰은 10개)
        int threadPoolSize = 10;

        long savedCouponId = COUPON.getId();
        log.info("Redis 쿠폰 ID: {}", savedCouponId);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            long userId = i;
            tasks.add(() -> {
                try {
                    couponFacade.couponFirstIssueWithRedis(new CouponCriteria.Issue(userId, savedCouponId));
                    successCount.incrementAndGet();
                    log.info("✅ Redis 쿠폰 발급 성공 - userId: {}", userId);
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.warn("❌ Redis 쿠폰 발급 실패 - userId: {}, message: {}", userId, e.getMessage());
                }
            });
        }

        // Act
        ConcurrentExecutor.execute(threadPoolSize, threadCount, tasks);

        // Assert
        log.info("🎯 Redis 쿠폰 발급 최종 결과 - 성공: {}, 실패: {}", successCount.get(), failureCount.get());
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        // Redis에 남은 쿠폰 수량 확인
        long remainingStock = redisCouponRepository.getCouponStock(savedCouponId);
        log.info("Redis에 남은 쿠폰 수량: {}", remainingStock);

        // DB에 저장된 쿠폰 수량 확인
        Coupon coupon = couponRepository.findById(savedCouponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        log.info("DB에 저장된 쿠폰 수량: {}", coupon.getQuantity());

        // 성공한 수가 10개이고, 남은 쿠폰 수량이 0인지 확인
        assertThat(successCount.get()).isEqualTo(10);
        assertThat(remainingStock).isEqualTo(0);
        assertThat(coupon.getQuantity()).isEqualTo(0);
    }
}