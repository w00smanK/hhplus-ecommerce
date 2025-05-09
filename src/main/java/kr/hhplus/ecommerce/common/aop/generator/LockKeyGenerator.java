package kr.hhplus.ecommerce.common.aop.generator;

import java.lang.reflect.Method;
import java.util.List;

public interface LockKeyGenerator {
    String generateKey(Method method, Object[] args, String prefix, String keyExpression);
    List<String> generateKeys(Method method, Object[] args, String prefix, String keyExpression);
}
