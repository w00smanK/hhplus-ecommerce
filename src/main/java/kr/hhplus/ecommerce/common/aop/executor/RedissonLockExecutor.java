package kr.hhplus.ecommerce.common.aop.executor;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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
            if (!isLocked) {
                throw new IllegalStateException("락 획득 실패: " + key);
            }
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException("락 처리 중 오류", e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
