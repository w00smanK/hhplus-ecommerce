package kr.hhplus.ecommerce.common.aop.annotation;

import kr.hhplus.ecommerce.common.aop.executor.LockExecutorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String prefix() default "";  // e.g. "'user:' + #userId"
    String key(); // SpEL expression
    long waitTime() default 5L; // 락 대기 시간 (초)
    long leaseTime() default 3L; // 락 보유 시간 (초)
    TimeUnit timeUnit() default TimeUnit.SECONDS; // 시간 단위
    LockExecutorType executor() default LockExecutorType.REDISSON;
}
