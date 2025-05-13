package kr.hhplus.ecommerce.interfaces.aop;

import kr.hhplus.ecommerce.MockTestSupport;
import kr.hhplus.ecommerce.common.aop.annotation.DistributedLock;
import kr.hhplus.ecommerce.common.aop.aspect.DistributedLockAspect;
import kr.hhplus.ecommerce.common.aop.executor.LockExecutor;
import kr.hhplus.ecommerce.common.aop.executor.LockExecutorType;
import kr.hhplus.ecommerce.common.aop.generator.LockKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@DisplayName("DistributedLockAspect 테스트")
@ExtendWith(MockitoExtension.class)
@Slf4j
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

        // DistributedLock 어노테이션 설정 - lenient() 사용
        lenient().when(distributedLock.prefix()).thenReturn("test:");
        lenient().when(distributedLock.key()).thenReturn("#testArg");
        lenient().when(distributedLock.waitTime()).thenReturn(5L);
        lenient().when(distributedLock.leaseTime()).thenReturn(3L);
        lenient().when(distributedLock.executor()).thenReturn(LockExecutorType.REDISSON);

        // LockKeyGenerator 설정 - 단일 키 케이스 - lenient() 사용
        lenient().when(keyGenerator.generateKeys(eq(method), eq(args), eq("test:"), eq("#testArg")))
                .thenReturn(Collections.singletonList("test:testArg"));

        // LockExecutor 설정 - lenient() 사용
        lenient().when(lockExecutor.getType()).thenReturn(LockExecutorType.REDISSON);
        lenient().when(lockExecutor.execute(anyString(), anyLong(), anyLong(), any(Callable.class)))
                .thenAnswer(invocation -> {
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
        verify(keyGenerator).generateKeys(eq(method), eq(args), eq("test:"), eq("#testArg"));
        verify(lockExecutor).execute(eq("test:testArg"), eq(5L), eq(3L), any(Callable.class));
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("분산락 AOP에서 예외가 발생하면 RuntimeException으로 감싸서 던진다")
    void applyLock_exception() throws Throwable {
        // given
        Exception testException = new Exception("Test Exception");
        given(joinPoint.proceed()).willThrow(testException);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            distributedLockAspect.applyLock(joinPoint, distributedLock)
        );

        assertEquals(testException, exception.getCause());
        verify(keyGenerator).generateKeys(eq(method), eq(args), eq("test:"), eq("#testArg"));
        verify(lockExecutor).execute(eq("test:testArg"), eq(5L), eq(3L), any(Callable.class));
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("다중 키를 사용하는 경우 executeWithMultiLock을 호출한다")
    void applyLock_multipleKeys() throws Throwable {
        // given
        List<String> multiKeys = List.of("test:key1", "test:key2", "test:key3");
        given(keyGenerator.generateKeys(eq(method), eq(args), eq("test:"), eq("#testArg")))
                .willReturn(multiKeys);
        given(lockExecutor.executeWithMultiLock(eq(multiKeys), anyLong(), anyLong(), any(Callable.class)))
                .willAnswer(invocation -> {
                    Callable<Object> task = invocation.getArgument(3);
                    return task.call();
                });
        given(joinPoint.proceed()).willReturn("multi-success");

        // when
        Object result = distributedLockAspect.applyLock(joinPoint, distributedLock);

        // then
        assertEquals("multi-success", result);
        verify(keyGenerator).generateKeys(eq(method), eq(args), eq("test:"), eq("#testArg"));
        verify(lockExecutor).executeWithMultiLock(eq(multiKeys), eq(5L), eq(3L), any(Callable.class));
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("LockExecutor가 없으면 IllegalStateException을 던진다")
    void applyLock_noExecutor() throws Throwable {
        // given
        LockExecutorType nonExistentType = mock(LockExecutorType.class);
        given(nonExistentType.toString()).willReturn("NON_EXISTENT");
        given(distributedLock.executor()).willReturn(nonExistentType);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
            distributedLockAspect.applyLock(joinPoint, distributedLock)
        );

        assertEquals("No LockExecutor found for: NON_EXISTENT", exception.getMessage());
        verify(keyGenerator).generateKeys(eq(method), eq(args), eq("test:"), eq("#testArg"));
        verify(joinPoint, never()).proceed();
    }

    // 테스트용 메서드
    private String testMethod(String testArg) {
        return "test";
    }
}
