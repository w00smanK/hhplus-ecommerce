package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.concurrency.support.ConcurrentExecutor;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("[통합테스트] CouponRedisFacade")
@Description("선착순 쿠폰 발급 Redis 테스트")
@ActiveProfiles("test")
@Slf4j
class CouponRedisFacadeTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponApplyRepository couponApplyRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Coupon COUPON;

    @BeforeEach
    void setUp() {
        // 테스트용 쿠폰 생성 (10개 수량)
        COUPON = couponRepository.save(new Coupon(1000L, 10));

        // Redis에 쿠폰 초기화
        couponApplyRepository.initializeCoupon(COUPON);
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
                    couponService.issueWithRedis(new CouponCommand.Issue(userId, savedCouponId));
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

        // 모든 작업이 완료된 후 Redis와 DB 동기화
        couponService.synchronizeCouponQuantity(savedCouponId);

        // Assert
        log.info("🎯 Redis 쿠폰 발급 최종 결과 - 성공: {}, 실패: {}", successCount.get(), failureCount.get());
        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);

        // Redis에 남은 쿠폰 수량 확인
        long remainingStock = couponApplyRepository.getCouponStock(savedCouponId);
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

    @Test
    @DisplayName("쿠폰 발급 요청 시각을 스코어로 사용하여 선착순 순서 보장")
    void couponFirstIssueWithRedis_timestampOrder() throws InterruptedException {
        Coupon testCoupon = couponRepository.save(new Coupon(1000L, 10));
        long couponId = testCoupon.getId();

        // Redis에 쿠폰 초기화 (타임스탬프 기반 스코어 사용)
        couponApplyRepository.initializeCoupon(testCoupon);

        // Redis에 저장된 쿠폰 정보 확인
        String couponKey = "coupon:" + couponId;
        Set<ZSetOperations.TypedTuple<String>> couponSet = redisTemplate.opsForZSet().rangeWithScores(couponKey, 0, -1);

        log.info("초기 쿠폰 정보 (Redis):");
        couponSet.forEach(tuple -> {
            log.info("멤버: {}, 스코어(타임스탬프): {}", tuple.getValue(), tuple.getScore());
        });

        List<Long> userIds = List.of(100L, 101L, 102L, 103L, 104L,105L, 106L, 107L, 108L, 109L);
        List<IssuedCoupon> issuedResults = new ArrayList<>();

        for (Long userId : userIds) {
            try {
                Thread.sleep(1000);
                IssuedCoupon result = couponService.issueWithRedis(new CouponCommand.Issue(userId, couponId));
                issuedResults.add(result);
                log.info("✅ 쿠폰 발급 성공 - userId: {}, 시간: {}", userId, System.currentTimeMillis());
            } catch (Exception e) {
                log.warn("❌ 쿠폰 발급 실패 - userId: {}, message: {}", userId, e.getMessage());
            }
        }

        // 모든 쿠폰이 발급되었는지 확인
        assertThat(issuedResults).hasSize(10);

        // Redis에 남은 쿠폰 수량 확인
        long remainingStock = couponApplyRepository.getCouponStock(couponId);
        assertThat(remainingStock).isEqualTo(0);

        // 발급된 쿠폰 정보 확인
        for (int i = 0; i < issuedResults.size(); i++) {
            log.info("발급된 쿠폰 #{} - userId: {}", i+1, issuedResults.get(i).getUserId());
        }

        // 발급된 쿠폰의 userId가 요청 순서대로인지 확인
        for (int i = 0; i < issuedResults.size(); i++) {
            assertThat(issuedResults.get(i).getUserId()).isEqualTo(userIds.get(i));
        }
    }
}
