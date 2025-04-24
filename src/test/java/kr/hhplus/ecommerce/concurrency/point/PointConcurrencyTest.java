package kr.hhplus.ecommerce.concurrency.point;

import kr.hhplus.ecommerce.concurrency.support.ConcurrentExecutor;
import kr.hhplus.ecommerce.domain.point.PointRepository;
import kr.hhplus.ecommerce.domain.point.PointService;
import kr.hhplus.ecommerce.domain.point.dto.PointCommand;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@Testcontainers
@DisplayName("포인트 동시성 테스트")
class PointConcurrencyTest {

    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private PointService pointService;

    @Test
    @DisplayName("[사용자 포인트 충전/사용] 동시성 테스트 - 낙관적 락")
    void chargePoint_concurrently() throws InterruptedException {
        // given
        Long userId = 1L;
        Point point = Point.create(userId);
        point.charge(1_000L);
        pointRepository.save(point);

        int threadCount = 2;
        int threadPoolSize = 2;

        PointCommand.Charge chargeCmd = PointCommand.Charge.of(userId, 500L);
        PointCommand.Use useCmd = PointCommand.Use.of(userId, 300L);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Runnable task = (i % 2 == 0)
                    ? () -> tryCharge(chargeCmd, successCount, failCount)
                    : () -> tryUse(useCmd, successCount, failCount);
            tasks.add(task);
        }

        // when
        ConcurrentExecutor.execute(threadPoolSize, threadCount, tasks);

        // then
        Point saved = pointRepository.findByUserId(userId).orElseThrow();
        log.info("✅ 최종 잔액: {}", saved.getAccount());

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }

    private void tryCharge(PointCommand.Charge command, AtomicInteger successCount, AtomicInteger failCount) {
        try {
            pointService.chargeWithLock(command);
            successCount.incrementAndGet();
        } catch (Exception e) {
            failCount.incrementAndGet();
            log.error("[❌ 충전 실패] {}", e.getMessage());
        }
    }

    private void tryUse(PointCommand.Use command, AtomicInteger successCount, AtomicInteger failCount) {
        try {
            pointService.useWithLock(command);
            successCount.incrementAndGet();
        } catch (Exception e) {
            failCount.incrementAndGet();
            log.error("[❌ 사용 실패] {}", e.getMessage());
        }
    }
}
