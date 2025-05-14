package kr.hhplus.ecommerce.interfaces.aop;

import kr.hhplus.ecommerce.MockTestSupport;
import kr.hhplus.ecommerce.common.aop.annotation.DistributedLock;
import kr.hhplus.ecommerce.common.aop.executor.LockExecutor;
import kr.hhplus.ecommerce.common.aop.executor.LockExecutorType;
import kr.hhplus.ecommerce.common.aop.factory.DistributedLockProxyFactory;
import kr.hhplus.ecommerce.common.aop.generator.LockKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("DistributedLockProxyFactory 테스트")
@ExtendWith(MockitoExtension.class)
@Slf4j
class DistributedLockProxyFactoryTest extends MockTestSupport {

    @Mock
    private LockKeyGenerator keyGenerator;

    @Mock
    private LockExecutor lockExecutor;

    private DistributedLockProxyFactory proxyFactory;

    @BeforeEach
    void setUp() {
        given(lockExecutor.getType()).willReturn(LockExecutorType.REDISSON);
        proxyFactory = new DistributedLockProxyFactory(keyGenerator, List.of(lockExecutor));
    }

    @Test
    @DisplayName("프록시 객체가 분산락을 사용하여 메서드를 실행한다")
    void proxyExecutesMethodWithLock() {
        // given
        given(keyGenerator.generateKeys(any(), anyString(), anyString()))
                .willReturn(Collections.singletonList("test:testArg"));

        given(lockExecutor.execute(anyString(), anyLong(), anyLong(), any(Callable.class)))
                .willAnswer(invocation -> {
                    String key = invocation.getArgument(0);
                    log.info("단일 락 실행됨: {}", key);
                    Callable<Object> task = invocation.getArgument(3);
                    return task.call();
                });

        TestService testService = new TestService();
        TestService proxy = proxyFactory.createProxy(testService);

        // when
        String result = proxy.testMethod("testArg");

        // then
        log.info("최종 결과: {}", result);
        assertEquals("test:testArg", result);
        verify(keyGenerator).generateKeys(any(), eq("test:"), eq("#arg"));
        verify(lockExecutor).execute(eq("test:testArg"), eq(5L), eq(3L), any(Callable.class));
    }

    @Test
    @DisplayName("프록시 객체가 예외를 정상적으로 처리한다")
    void proxyHandlesExceptions() {
        // given
        given(keyGenerator.generateKeys(any(), anyString(), anyString()))
                .willReturn(Collections.singletonList("test:testArg"));

        given(lockExecutor.execute(anyString(), anyLong(), anyLong(), any(Callable.class)))
                .willAnswer(invocation -> {
                    String key = invocation.getArgument(0);
                    log.info("단일 락 실행됨: {}", key);
                    Callable<Object> task = invocation.getArgument(3);
                    return task.call();
                });

        ExceptionTestService testService = new ExceptionTestService();
        ExceptionTestService proxy = proxyFactory.createProxy(testService);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                proxy.exceptionMethod("testArg")
        );
        assertEquals("Test Exception", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("프록시 객체가 다중 락을 사용하여 메서드를 실행한다")
    void proxyExecutesMethodWithMultiLock() {
        // given
        List<String> keys = List.of("test:1", "test:2", "test:3");
        given(keyGenerator.generateKeys(any(), anyString(), anyString()))
                .willReturn(keys);

        given(lockExecutor.executeWithMultiLock(anyList(), anyLong(), anyLong(), any(Callable.class)))
                .willAnswer(invocation -> {
                    List<String> k = invocation.getArgument(0);
                    log.info("다중 락 실행됨: {}", k);
                    Callable<Object> task = invocation.getArgument(3);
                    return task.call();
                });

        MultiLockTestService service = new MultiLockTestService();
        MultiLockTestService proxy = proxyFactory.createProxy(service);

        // when
        String result = proxy.multiLockMethod("multi");

        // then
        log.info("다중 락 테스트 결과: {}", result);
        assertEquals("multi:multi", result);
        verify(lockExecutor).executeWithMultiLock(eq(keys), eq(5L), eq(3L), any(Callable.class));
    }

    // 단일 락 대상 클래스
    static class TestService {
        @DistributedLock(prefix = "test:", key = "#arg")
        public String testMethod(String arg) {
            return "test:" + arg;
        }
    }

    // 예외 발생 클래스
    static class ExceptionTestService {
        @DistributedLock(prefix = "test:", key = "#arg")
        public String exceptionMethod(String arg) {
            throw new IllegalArgumentException("Test Exception");
        }
    }

    // 다중 락 테스트용 클래스
    static class MultiLockTestService {
        @DistributedLock(prefix = "test", key = "#arg")
        public String multiLockMethod(String arg) {
            return "multi:" + arg;
        }
    }
}