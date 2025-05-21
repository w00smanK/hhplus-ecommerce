package kr.hhplus.ecommerce.infra.rank;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Redis를 이용한 인기 상품 랭킹 저장소
 */
@Repository
@RequiredArgsConstructor
public class RankRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String DAILY_RANK_PREFIX = "product:rank:daily:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 일별 인기 상품 랭킹 추가
     *
     * @param productId 상품 ID
     * @param quantity 판매량
     * @param date 날짜
     */
    public void addDailyRank(Long productId, Long quantity, LocalDate date) {
        String key = DAILY_RANK_PREFIX + date.format(DATE_FORMATTER);
        redisTemplate.opsForZSet().add(key, productId.toString(), quantity);
    }

    /**
     * 특정 날짜 범위의 상위 인기 상품 목록 조회
     *
     * @param date 시작 날짜
     * @param days 조회할 일수
     * @param limit 상위 N개
     * @return 상위 N개 상품 ID 목록
     */
    public List<Long> getTopProductsByDays(LocalDate date, int days, int limit) {
        List<Long> result = new ArrayList<>();
        
        // 지정된 날짜의 키 생성
        String key = DAILY_RANK_PREFIX + date.format(DATE_FORMATTER);
        
        // Redis에서 상위 N개 항목 조회 (높은 점수 순으로)
        Set<ZSetOperations.TypedTuple<Object>> rankedSet = 
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);
        
        if (rankedSet != null && !rankedSet.isEmpty()) {
            for (ZSetOperations.TypedTuple<Object> tuple : rankedSet) {
                if (tuple.getValue() != null) {
                    result.add(Long.valueOf(tuple.getValue().toString()));
                }
            }
        }
        
        return result;
    }
}
