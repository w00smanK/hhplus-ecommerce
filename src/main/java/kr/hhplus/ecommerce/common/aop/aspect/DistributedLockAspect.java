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
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class DistributedLockAspect {
    private final LockKeyGenerator keyGenerator;
    private final Map<LockExecutorType, LockExecutor> executorMap;


    public DistributedLockAspect(
            LockKeyGenerator keyGenerator,
            List<LockExecutor> executors) {
        this.keyGenerator = keyGenerator;
        this.executorMap = executors.stream()
                .collect(Collectors.toMap(LockExecutor::getType, Function.identity()));
    }

    @Around("@annotation(distributedLock)")
    public Object applyLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String key = keyGenerator.generateKey(method, joinPoint.getArgs(), distributedLock.prefix(), distributedLock.key());
        LockExecutor executor = executorMap.get(distributedLock.executor());

        if (executor == null) {
            throw new IllegalStateException("No LockExecutor found for: " + distributedLock.executor());
        }
        return executor.execute(key, distributedLock.waitTime(), distributedLock.leaseTime(), () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e); // wrap
            }
        });

    }
}
