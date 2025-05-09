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

    @Override
    public <T> T executeWithMultiLock(List<String> keys, long waitTime, long leaseTime, Callable<T> task) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("락 키 목록이 비어있습니다.");
        }

        // 단일 키인 경우 기존 메서드 사용
        if (keys.size() == 1) {
            return execute(keys.get(0), waitTime, leaseTime, task);
        }

        // 여러 키에 대한 락 생성
        List<RLock> locks = new ArrayList<>(keys.size());
        for (String key : keys) {
            locks.add(redissonClient.getLock(key));
        }

        // RedissonMultiLock 생성
        RedissonMultiLock multiLock = new RedissonMultiLock(locks.toArray(new RLock[0]));
        boolean isLocked = false;

        try {
            log.debug("다중 락 획득 시도: {}", keys);
            isLocked = multiLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new IllegalStateException("다중 락 획득 실패: " + keys);
            }

            log.debug("다중 락 획득 성공: {}", keys);
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException("다중 락 처리 중 오류", e);
        } finally {
            if (isLocked && multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
                log.debug("다중 락 해제: {}", keys);
            }
        }
    }
}
