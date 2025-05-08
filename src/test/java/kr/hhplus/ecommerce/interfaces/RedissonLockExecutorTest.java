package kr.hhplus.ecommerce.interfaces;

import kr.hhplus.ecommerce.common.aop.executor.RedissonLockExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@DisplayName("RedissonLockExecutor 테스트")
class RedissonLockExecutorTest {

    private RedissonClient redissonClient;
    private RLock rLock;
    private RedissonLockExecutor executor;

    @BeforeEach
    void setUp() {
        redissonClient = mock(RedissonClient.class);
        rLock = mock(RLock.class);
        executor = new RedissonLockExecutor(redissonClient);

        given(redissonClient.getLock("test:lock")).willReturn(rLock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("락을 획득하면 로직을 실행하고 해제한다")
        void success() throws Exception {
            given(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);

            String result = executor.execute("test:lock", 1, 3, () -> "success");

            assertEquals("success", result);
            then(rLock).should().unlock();
        }

        @Test
        @DisplayName("락 획득에 실패하면 예외를 던진다")
        void failToAcquireLock() throws Exception {
            given(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).willReturn(false);

            assertThrows(RuntimeException.class, () ->
                    executor.execute("test:lock", 1, 3, () -> "fail")
            );
        }

        @Test
        @DisplayName("락 획득 도중 예외가 발생하면 RuntimeException으로 감싼다")
        void throwsExceptionDuringLock() throws Exception {
            given(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS)))
                    .willThrow(new InterruptedException());

            assertThrows(RuntimeException.class, () ->
                    executor.execute("test:lock", 1, 3, () -> "fail")
            );
        }

        @Test
        @DisplayName("현재 쓰레드가 락을 보유하지 않으면 unlock을 호출하지 않는다")
        void noUnlockWhenNotHeld() throws Exception {
            given(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(false);

            executor.execute("test:lock", 1, 3, () -> "no-unlock");

            then(rLock).should(never()).unlock();
        }
    }
}
