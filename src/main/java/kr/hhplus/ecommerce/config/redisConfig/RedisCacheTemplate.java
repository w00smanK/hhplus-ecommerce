package kr.hhplus.ecommerce.config.redisConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis 캐시 템플릿
 * 캐시 조회 및 저장을 위한 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCacheTemplate {

    private final RedisTemplate<String, Object> redisObjectTemplate;
    private static final long DEFAULT_EXPIRE_SECONDS = 86400; // 1일

    /**
     * 캐시 조회
     */
    public <T> Optional<T> get(String cacheType, String key, Class<T> clazz) {
        String cacheKey = getCacheKey(cacheType, key);
        try {
            Object value = redisObjectTemplate.opsForValue().get(cacheKey);
            if (value == null) {
                log.debug("캐시 미스 - 타입: {}, 키: {}", cacheType, key);
                return Optional.empty();
            }

            if (clazz.isInstance(value)) {
                log.debug("캐시 히트 - 타입: {}, 키: {}", cacheType, key);
                return Optional.of(clazz.cast(value));
            } else {
                log.warn("캐시 타입 불일치 - 타입: {}, 키: {}, 예상 타입: {}, 실제 타입: {}", 
                        cacheType, key, clazz.getName(), value.getClass().getName());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("캐시 조회 실패 - 타입: {}, 키: {}, 오류: {}", cacheType, key, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 캐시 저장
     */
    public <T> T put(String cacheType, String key, T value) {
        return put(cacheType, key, value, DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 캐시 저장 (만료 시간 지정)
     */
    public <T> T put(String cacheType, String key, T value, long expireSeconds) {
        String cacheKey = getCacheKey(cacheType, key);
        try {
            redisObjectTemplate.opsForValue().set(cacheKey, value, expireSeconds, TimeUnit.SECONDS);
            log.debug("캐시 저장 - 타입: {}, 키: {}, 만료: {}초", cacheType, key, expireSeconds);
        } catch (Exception e) {
            log.error("캐시 저장 실패 - 타입: {}, 키: {}, 오류: {}", cacheType, key, e.getMessage());
        }
        return value;
    }

    /**
     * 캐시 삭제
     */
    public void delete(String cacheType, String key) {
        String cacheKey = getCacheKey(cacheType, key);
        try {
            redisObjectTemplate.delete(cacheKey);
            log.debug("캐시 삭제 - 타입: {}, 키: {}", cacheType, key);
        } catch (Exception e) {
            log.error("캐시 삭제 실패 - 타입: {}, 키: {}, 오류: {}", cacheType, key, e.getMessage());
        }
    }

    /**
     * 캐시 키 생성
     */
    private String getCacheKey(String cacheType, String key) {
        return cacheType + ":" + key;
    }
}
