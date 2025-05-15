package kr.hhplus.ecommerce.application.coupon;

import kr.hhplus.ecommerce.application.coupon.dto.CouponCriteria;
import kr.hhplus.ecommerce.concurrency.support.ConcurrentExecutor;
import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
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
@DisplayName("[통합테스트] CouponFacade")
@Description("선착순 쿠폰 발급 테스트")
@ActiveProfiles("test")
@Slf4j
class CouponFacadeTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    private Coupon COUPON;

    @BeforeEach
    void setUp() {
        COUPON = couponRepository.save(new Coupon(1000L, 10));
    }

    @Test
    @DisplayName("선착순 쿠폰 발급 성공")
    void firstComeFirstIssue_success() throws InterruptedException {
        // Arrange
        int threadCount = 15;
        int threadPoolSize = 10;

        long savedCouponId = COUPON.getId();
        log.info("쿠폰 ID: {}", savedCouponId);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            long idx = i;
            tasks.add(() -> {
                try {
                    couponFacade.couponFirstIssue(new CouponCriteria.Issue(idx,savedCouponId));
                    successCount.incrementAndGet();
                    log.info("✅ 쿠폰 발급 성공 - idx: {}", idx);
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.warn("❌ 쿠폰 발급 실패 - idx: {}, message: {}", idx, e.getMessage());
                }
            });
        }

        // Act
        ConcurrentExecutor.execute(threadPoolSize, threadCount, tasks);

        // Assert
        log.info("🎯 쿠폰 발급 최종 결과 - 성공: {}, 실패: {}", successCount.get(), failureCount.get());
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        Coupon coupon = couponRepository.findById(COUPON.getId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        assertThat(coupon.getQuantity()).isEqualTo(0);
    }
}
