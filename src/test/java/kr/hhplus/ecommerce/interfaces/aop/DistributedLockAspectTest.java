package kr.hhplus.ecommerce.interfaces.aop;

import kr.hhplus.ecommerce.MockTestSupport;
import kr.hhplus.ecommerce.common.aop.annotation.DistributedLock;
import kr.hhplus.ecommerce.common.aop.aspect.DistributedLockAspect;
import kr.hhplus.ecommerce.common.aop.executor.LockExecutor;
import kr.hhplus.ecommerce.common.aop.executor.LockExecutorType;
import kr.hhplus.ecommerce.common.aop.generator.LockKeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("DistributedLockAspect 테스트")
class DistributedLockAspectTest extends MockTestSupport {

    @Mock
    private LockKeyGenerator keyGenerator;

    @Mock
    private LockExecutor lockExecutor;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private DistributedLock distributedLock;

    @InjectMocks
    private DistributedLockAspect distributedLockAspect;

    private Method method;
    private Object[] args;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        // 테스트용 메서드 설정
        method = getClass().getDeclaredMethod("testMethod", String.class);
        args = new Object[]{"testArg"};

        // Mock 설정
        given(joinPoint.getSignature()).willReturn(methodSignature);
        given(methodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(args);

        // DistributedLock 어노테이션 설정
        given(distributedLock.prefix()).willReturn("test:");
        given(distributedLock.key()).willReturn("#testArg");
        given(distributedLock.waitTime()).willReturn(5L);
        given(distributedLock.leaseTime()).willReturn(3L);
        given(distributedLock.executor()).willReturn(LockExecutorType.REDISSON);

        // LockKeyGenerator 설정
        given(keyGenerator.generateKey(eq(method), eq(args), eq("test:"), eq("#testArg")))
                .willReturn("test:testArg");

        // LockExecutor 설정
        given(lockExecutor.getType()).willReturn(LockExecutorType.REDISSON);
        given(lockExecutor.execute(eq("test:testArg"), eq(5L), eq(3L), any(Callable.class)))
                .willAnswer(invocation -> {
                    Callable<Object> task = invocation.getArgument(3);
                    return task.call();
                });

        // DistributedLockAspect 설정
        distributedLockAspect = new DistributedLockAspect(keyGenerator, List.of(lockExecutor));
    }

    @Test
    @DisplayName("분산락 AOP가 정상적으로 동작한다")
    void applyLock_success() throws Throwable {
        // given
        given(joinPoint.proceed()).willReturn("success");

        // when
        Object result = distributedLockAspect.applyLock(joinPoint, distributedLock);

        // then
        assertEquals("success", result);
        verify(keyGenerator).generateKey(eq(method), eq(args), eq("test:"), eq("#testArg"));
        verify(lockExecutor).execute(eq("test:testArg"), eq(5L), eq(3L), any(Callable.class));
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("분산락 AOP에서 예외가 발생하면 RuntimeException으로 감싸서 던진다")
    void applyLock_exception() throws Throwable {
        // given
        given(joinPoint.proceed()).willThrow(new Exception("Test Exception"));

        try {
            // when
            distributedLockAspect.applyLock(joinPoint, distributedLock);
        } catch (RuntimeException e) {
            // then
            assertEquals("java.lang.Exception: Test Exception", e.getCause().toString());
            verify(keyGenerator).generateKey(eq(method), eq(args), eq("test:"), eq("#testArg"));
            verify(lockExecutor).execute(eq("test:testArg"), eq(5L), eq(3L), any(Callable.class));
            verify(joinPoint).proceed();
        }
    }

    @Test
    @DisplayName("LockExecutor가 없으면 IllegalStateException을 던진다")
    void applyLock_noExecutor() throws Throwable {
        // given
        LockExecutorType nonExistentType = mock(LockExecutorType.class);
        given(nonExistentType.toString()).willReturn("NON_EXISTENT");
        given(distributedLock.executor()).willReturn(nonExistentType);

        try {
            // when
            distributedLockAspect.applyLock(joinPoint, distributedLock);
        } catch (IllegalStateException e) {
            // then
            assertEquals("No LockExecutor found for: NON_EXISTENT", e.getMessage());
            verify(keyGenerator).generateKey(eq(method), eq(args), eq("test:"), eq("#testArg"));
            verify(joinPoint, never()).proceed();
        }
    }

    // 테스트용 메서드
    private String testMethod(String testArg) {
        return "test";
    }
}
