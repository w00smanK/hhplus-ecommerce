package kr.hhplus.ecommerce.common.aop.generator;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class SpELLockKeyGenerator implements LockKeyGenerator {
    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public String generateKey(Method method, Object[] args, String prefix, String keyExpression) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        MethodSignature methodSignature = (MethodSignature) MethodSignature.class.cast(method);
        String[] paramNames = methodSignature.getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String parsedKey = parser.parseExpression(keyExpression).getValue(context, String.class);
        return prefix + parsedKey;
    }
}
