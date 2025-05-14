package kr.hhplus.ecommerce.common.aop.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockExecutor implements LockExecutor {

    private final RedissonClient redissonClient;

    @Override
    public LockExecutorType getType() {
        return LockExecutorType.REDISSON;
    }

    @Override
    public <T> T execute(String key, long waitTime, long leaseTime, Callable<T> task) {
        RLock lock = redissonClient.getLock(key);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            log.info("🔐 락 시도 - key: {}, waitTime: {}s, leaseTime: {}s", key, waitTime, leaseTime);
            if (!isLocked) {
                log.warn("❌ 락 획득 실패 - key: {}", key);
                throw new IllegalStateException("락 획득 실패: " + key);
            }
            log.info("✅ 락 획득 성공 - key: {}, threadId: {}", key, Thread.currentThread().getId());
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException("락 처리 중 오류", e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public <T> T executeWithMultiLock(List<String> keys, long waitTime, long leaseTime, Callable<T> task) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("락 키 목록이 비어있습니다.");
        }

        // 단일 키인 경우 기존 메서드 사용
        if (keys.size() == 1) {
            return execute(keys.get(0), waitTime, leaseTime, task);
        }

        List<RLock> locks = new ArrayList<>(keys.size());
        for (String key : keys) {
            locks.add(redissonClient.getLock(key));
        }

        RedissonMultiLock multiLock = new RedissonMultiLock(locks.toArray(new RLock[0]));
        boolean isLocked = false;

        try {
            log.info("🔐 다중 락 시도 - keys: {}, waitTime: {}s, leaseTime: {}s, threadId: {}",
                    keys, waitTime, leaseTime, Thread.currentThread().getId());

            isLocked = multiLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

            if (!isLocked) {
                log.warn("❌ 다중 락 획득 실패 - keys: {}, threadId: {}", keys, Thread.currentThread().getId());
                throw new IllegalStateException("다중 락 획득 실패: " + keys);
            }

            log.info("✅ 다중 락 획득 성공 - keys: {}, threadId: {}", keys, Thread.currentThread().getId());

            return task.call();

        } catch (Exception e) {
            log.error("💥 다중 락 처리 중 예외 - keys: {}, error: {}, threadId: {}", keys, e.getMessage(), Thread.currentThread().getId(), e);
            throw new RuntimeException("다중 락 처리 중 오류", e);
        } finally {
            if (isLocked && multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
                log.info("🔓 다중 락 해제 완료 - keys: {}, threadId: {}", keys, Thread.currentThread().getId());
            }
        }
    }
}
