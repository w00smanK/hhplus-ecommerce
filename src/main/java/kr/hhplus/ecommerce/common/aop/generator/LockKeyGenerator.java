package kr.hhplus.ecommerce.common.aop.generator;

import java.lang.reflect.Method;

public interface LockKeyGenerator {
    String generateKey(Method method, Object[] args, String prefix, String keyExpression);
}
