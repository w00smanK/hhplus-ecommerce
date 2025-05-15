package kr.hhplus.ecommerce.common.aop.generator;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.List;

public interface LockKeyGenerator {
//    String generateKey(Method method, Object[] args, String prefix, String keyExpression);
//    List<String> generateKeys(Method method, Object[] args, String prefix, String keyExpression);
    String generateKey(ProceedingJoinPoint joinPoint, String prefix, String keyExpression);
    List<String> generateKeys(ProceedingJoinPoint joinPoint, String prefix, String keyExpression);
}
