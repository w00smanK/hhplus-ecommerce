package kr.hhplus.ecommerce.concurrency.coupon;

import kr.hhplus.ecommerce.concurrency.support.ConcurrentExecutor;
import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Java6Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@Testcontainers
@DisplayName("선착순 쿠폰 발급 동시성 테스트")
public class CouponConcurrencyTest {


    @Autowired
    CouponService couponService;

    @Autowired
    CouponRepository couponRepository;

    private Long couponId;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @BeforeEach
    void setUp() {
        // 테스트 전에 쿠폰 초기화 (수량 10)
        Coupon coupon = couponRepository.save(Coupon.builder()
                .discountPrice(1000L)
                .quantity(10)
                .build());
        couponId = coupon.getId();
    }

    @Test
    @DisplayName("[쿠폰 발급] 동시성 테스트 - 낙관적 락")
    void issueCoupon_concurrently() throws InterruptedException {
        int threadCount = 100;
        int threadPoolSize = 10;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        List<Runnable> tasks = getRunnableList(threadCount, successCount, failureCount);

        ConcurrentExecutor.execute(threadPoolSize, threadCount, tasks);

        log.info("✅ 성공: {}, 실패: {}", successCount.get(), failureCount.get());

        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findAll();
        assertThat(issuedCoupons.size()).isEqualTo(10); // 수량 제한만큼만 발급
        assertThat(successCount.get()).isEqualTo(10);   // 실제 성공 수

        Coupon coupon = couponRepository.findById(couponId).orElseThrow();
        assertThat(coupon.getQuantity()).isEqualTo(0);
    }

    @NotNull
    private List<Runnable> getRunnableList(int threadCount, AtomicInteger successCount, AtomicInteger failureCount) {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final long userId = i + 1;
            tasks.add(() -> {
                try {
//                    couponService.issueWithPessimisticLock(new CouponCommand.Issue(userId, couponId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    log.error("[쿠폰 발급 실패] userId={}, reason={}", userId, e.getMessage());
                }
            });
        }
        return tasks;
    }

}
