package kr.hhplus.ecommerce.config.redisConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 캐시 클리너
 * 테스트 등에서 캐시를 초기화하기 위한 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCacheCleaner {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 모든 캐시 삭제
     */
    public void clean() {
        try {
            redisTemplate.execute((RedisConnection connection) -> {
                connection.flushAll();
                return null;
            });
            log.info("Redis 캐시 초기화 완료");
        } catch (Exception e) {
            log.error("Redis 캐시 초기화 실패: {}", e.getMessage());
        }
    }

    /**
     * 특정 패턴의 캐시 삭제
     * @param pattern 삭제할 캐시 키 패턴 (예: "popular-products:*")
     */
    public void cleanByPattern(String pattern) {
        try {
            redisTemplate.execute((RedisConnection connection) -> {
                connection.keys(pattern.getBytes()).forEach(key -> {
                    connection.del(key);
                });
                return null;
            });
            log.info("Redis 캐시 초기화 완료 - 패턴: {}", pattern);
        } catch (Exception e) {
            log.error("Redis 캐시 초기화 실패 - 패턴: {}, 오류: {}", pattern, e.getMessage());
        }
    }

    /**
     * 특정 캐시 타입의 모든 캐시 삭제
     * @param cacheType 삭제할 캐시 타입
     */
    public void cleanByType(String cacheType) {
        cleanByPattern(cacheType + ":*");
    }
}