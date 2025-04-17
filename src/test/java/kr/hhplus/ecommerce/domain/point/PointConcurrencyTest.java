package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.application.point.PointFacade;
import kr.hhplus.ecommerce.application.point.dto.PointCriteria;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import kr.hhplus.ecommerce.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("PointFacade 동시성 테스트")
class PointConcurrencyTest {
    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(new User(1L, "StressUser"));
        pointRepository.save(new Point(user.getId(), 1L, 0L));
        userId = user.getId();
    }

    @Test
    @DisplayName("대량 동시 충전 시 최종 금액 정확성 검증")
    void stressChargeTest() throws InterruptedException {
        int threads = 500; //
        long chargeAmount = 1000L;

        ExecutorService service = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            service.execute(() -> {
                try {
                    pointFacade.charge(new PointCriteria.Charge(userId, chargeAmount));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Point point = pointRepository.findBy(userId).orElseThrow();
        assertThat(point.getAccount()).isEqualTo(chargeAmount * threads);
    }

    @Test
    @DisplayName("충전 중 조회 시 정합성 보장 여부")
    void chargeAndReadConcurrently() throws InterruptedException {
        int chargeThreads = 300;
        int readThreads = 200;

        ExecutorService service = Executors.newFixedThreadPool(64);
        CountDownLatch latch = new CountDownLatch(chargeThreads + readThreads);

        for (int i = 0; i < chargeThreads; i++) {
            service.submit(() -> {
                pointFacade.charge(new PointCriteria.Charge(userId, 1000L));
                latch.countDown();
            });
        }

        for (int i = 0; i < readThreads; i++) {
            service.submit(() -> {
                pointFacade.findPoint(new PointCriteria.Find(userId));
                latch.countDown();
            });
        }

        latch.await();
        Point point = pointRepository.findBy(userId).orElseThrow();
        assertThat(point.getAccount()).isEqualTo(1000L * chargeThreads);
    }
}
