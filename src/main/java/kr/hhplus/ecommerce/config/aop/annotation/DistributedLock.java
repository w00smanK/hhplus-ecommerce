package kr.hhplus.ecommerce.config.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String key();           // e.g. "'user:' + #userId"
    long waitTime() default 5L; // 락 대기 시간 (초)
    long leaseTime() default 10L; // 락 보유 시간 (초)
}
