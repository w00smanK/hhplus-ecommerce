package kr.hhplus.ecommerce.common.aop.factory;

import kr.hhplus.ecommerce.common.aop.annotation.DistributedLock;
import kr.hhplus.ecommerce.common.aop.aspect.DistributedLockAspect;
import kr.hhplus.ecommerce.common.aop.executor.LockExecutor;
import kr.hhplus.ecommerce.common.aop.executor.LockExecutorType;
import kr.hhplus.ecommerce.common.aop.generator.LockKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AspectJProxyFactory를 사용하여 분산 락 프록시를 생성하는 팩토리 클래스
 */
@Component
@RequiredArgsConstructor
public class DistributedLockProxyFactory {

    private final LockKeyGenerator keyGenerator;
    private final List<LockExecutor> lockExecutors;

    /**
     * 주어진 대상 객체에 대한 분산 락 프록시를 생성합니다.
     * 대상 객체의 메서드 중 @DistributedLock 어노테이션이 있는 메서드는
     * 분산 락을 사용하여 실행됩니다.
     *
     * @param target 프록시를 생성할 대상 객체
     * @param <T> 대상 객체의 타입
     * @return 분산 락 기능이 추가된 프록시 객체
     */
    public <T> T createProxy(T target) {
        // AspectJProxyFactory 생성
        AspectJProxyFactory factory = new AspectJProxyFactory(target);

        // DistributedLockAspect 생성 및 추가
        DistributedLockAspect aspect = new DistributedLockAspect(keyGenerator, lockExecutors);
        factory.addAspect(aspect);

        // 프록시 생성 및 반환
        return factory.getProxy();
    }
}
