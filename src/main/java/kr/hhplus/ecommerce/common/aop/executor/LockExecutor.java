package kr.hhplus.ecommerce.common.aop.executor;

import java.util.concurrent.Callable;

public interface LockExecutor {
    LockExecutorType getType();

    <T> T execute(String key, long waitTime, long leaseTime, Callable<T> task);
}
