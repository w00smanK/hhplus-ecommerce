package kr.hhplus.ecommerce.common.aop.generator;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class SpELLockKeyGenerator implements LockKeyGenerator {
    private final ExpressionParser parser = new SpelExpressionParser();
    private static final Pattern WILDCARD_PATTERN = Pattern.compile("\\[(\\*)]");

    @Override
    public String generateKey(ProceedingJoinPoint joinPoint, String prefix, String keyExpression) {
        List<String> keys = generateKeys(joinPoint, prefix, keyExpression);
        return keys.isEmpty() ? prefix : keys.get(0);
    }

    @Override
    public List<String> generateKeys(ProceedingJoinPoint joinPoint, String prefix, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        Matcher matcher = WILDCARD_PATTERN.matcher(keyExpression);
        if (matcher.find()) {
            return generateMultipleKeys(context, prefix, keyExpression);
        } else {
            String parsedKey = parser.parseExpression(keyExpression).getValue(context, String.class);
            return Collections.singletonList(prefix + ":" + parsedKey);
        }
    }

    private List<String> generateMultipleKeys(EvaluationContext context, String prefix, String keyExpression) {
        String propertyPath = keyExpression.substring(0, keyExpression.indexOf("[*]"));
        Object collection = parser.parseExpression(propertyPath).getValue(context);

        if (collection instanceof Collection<?> items) {
            List<String> keys = new ArrayList<>();
            int index = 0;
            for (Object item : items) {
                String indexedExpr = keyExpression.replace("[*]", "[" + index + "]");
                String parsedKey = parser.parseExpression(indexedExpr).getValue(context, String.class);
                keys.add(prefix + ":" + parsedKey);
                index++;
            }
            return keys;
        }

        return Collections.emptyList();
    }

//    /**
//     * 테스트용 메서드
//     */
//    public List<String> generateMultipleKeysForTest(Object criteria, String prefix, String keyExpression) {
//        StandardEvaluationContext context = new StandardEvaluationContext();
//        context.setVariable("criteria", criteria);
//
//        // Check if the expression contains a wildcard
//        Matcher matcher = WILDCARD_PATTERN.matcher(keyExpression);
//        if (matcher.find()) {
//            return generateMultipleKeys(context, prefix, keyExpression);
//        } else {
//            // No wildcard, generate a single key
//            String parsedKey = parser.parseExpression(keyExpression).getValue(context, String.class);
//            return Collections.singletonList(prefix + ":" + parsedKey);
//        }
//    }
}
