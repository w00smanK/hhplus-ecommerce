package kr.hhplus.ecommerce.common.aop.aspect;

import kr.hhplus.ecommerce.common.aop.annotation.DistributedLock;
import kr.hhplus.ecommerce.common.aop.executor.LockExecutor;
import kr.hhplus.ecommerce.common.aop.executor.LockExecutorType;
import kr.hhplus.ecommerce.common.aop.generator.LockKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DistributedLockAspect {
    private final LockKeyGenerator keyGenerator;
    private final Map<LockExecutorType, LockExecutor> executorMap;

    public DistributedLockAspect(LockKeyGenerator keyGenerator, List<LockExecutor> lockExecutors) {
        this.keyGenerator = keyGenerator;
        this.executorMap = lockExecutors.stream()
                .collect(Collectors.toMap(LockExecutor::getType, Function.identity()));
    }


    @Around("@annotation(kr.hhplus.ecommerce.common.aop.annotation.DistributedLock)")
    public Object applyLock(ProceedingJoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);

        // 키 표현식에 와일드카드가 있는지 확인하여 단일 또는 다중 키 생성
        List<String> keys = keyGenerator.generateKeys(joinPoint, annotation.prefix(), annotation.key());
        log.info("Lock keys: {}", keys);
        LockExecutor executor = executorMap.get(annotation.executor());

        if (executor == null) {
            throw new IllegalStateException("No LockExecutor found for: " + annotation.executor());
        }

        // 키가 여러 개인 경우 다중 락 실행
        if (keys.size() > 1) {
            log.debug("다중 락 실행: {}", keys);
            return executor.executeWithMultiLock(keys, annotation.waitTime(), annotation.leaseTime(), () -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            // 키가 하나인 경우 단일 락 실행
            String key = keys.isEmpty() ? annotation.prefix() : keys.get(0);
            log.debug("단일 락 실행: {}", key);
            return executor.execute(key, annotation.waitTime(), annotation.leaseTime(), () -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
