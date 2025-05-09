package kr.hhplus.ecommerce.common.aop.generator;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SpELLockKeyGenerator implements LockKeyGenerator {
    private final ExpressionParser parser = new SpelExpressionParser();
    private static final Pattern WILDCARD_PATTERN = Pattern.compile("\\[(\\*)]");

    @Override
    public String generateKey(Method method, Object[] args, String prefix, String keyExpression) {
        List<String> keys = generateKeys(method, args, prefix, keyExpression);
        return keys.isEmpty() ? prefix : keys.get(0);
    }

    @Override
    public List<String> generateKeys(Method method, Object[] args, String prefix, String keyExpression) {
        StandardEvaluationContext context = createEvaluationContext(method, args);

        // Check if the expression contains a wildcard
        Matcher matcher = WILDCARD_PATTERN.matcher(keyExpression);
        if (matcher.find()) {
            return generateMultipleKeys(context, prefix, keyExpression);
        } else {
            // No wildcard, generate a single key
            String parsedKey = parser.parseExpression(keyExpression).getValue(context, String.class);
            return Collections.singletonList(prefix + ":" + parsedKey);
        }
    }

    private StandardEvaluationContext createEvaluationContext(Method method, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        MethodSignature methodSignature = (MethodSignature) MethodSignature.class.cast(method);
        String[] paramNames = methodSignature.getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        return context;
    }

    private List<String> generateMultipleKeys(EvaluationContext context, String prefix, String keyExpression) {
        // Replace [*] with [0] to get the collection
        String collectionExpression = WILDCARD_PATTERN.matcher(keyExpression).replaceFirst("[0]");
        String propertyPath = keyExpression.substring(0, keyExpression.indexOf("[*]"));

        // Get the collection
        Object collection = parser.parseExpression(propertyPath).getValue(context);
        if (collection == null) {
            return Collections.emptyList();
        }

        if (collection instanceof Collection<?>) {
            List<String> keys = new ArrayList<>();
            int index = 0;

            for (Object item : (Collection<?>) collection) {
                // Replace [*] with [index] to get each item
                String indexedExpression = keyExpression.replace("[*]", "[" + index + "]");
                String parsedKey = parser.parseExpression(indexedExpression).getValue(context, String.class);
                keys.add(prefix + ":" + parsedKey);
                index++;
            }

            return keys;
        }

        return Collections.emptyList();
    }

    /**
     * 테스트용 메서드
     */
    public List<String> generateMultipleKeysForTest(Object criteria, String prefix, String keyExpression) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("criteria", criteria);

        // Check if the expression contains a wildcard
        Matcher matcher = WILDCARD_PATTERN.matcher(keyExpression);
        if (matcher.find()) {
            return generateMultipleKeys(context, prefix, keyExpression);
        } else {
            // No wildcard, generate a single key
            String parsedKey = parser.parseExpression(keyExpression).getValue(context, String.class);
            return Collections.singletonList(prefix + ":" + parsedKey);
        }
    }
}
