package kr.hhplus.ecommerce.infra.rank;

import kr.hhplus.ecommerce.domain.rank.RankRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 인기 상품 Redis 저장소 구현체
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RankRepositoryImpl implements RankRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String DAILY_RANK_KEY_PREFIX = "ranking:daily:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public void addDailyRank(Long productId, Long quantity, LocalDate date) {
        String rankKey = getDailyRankKey(date);
        
        // Sorted Set에 상품 ID와 주문 수량 추가 (score는 주문 수량)
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Double currentScore = zSetOps.score(rankKey, productId.toString());
        
        // 이미 존재하는 경우 수량 증가, 없는 경우 새로 추가
        if (currentScore != null) {
            zSetOps.incrementScore(rankKey, productId.toString(), quantity);
            log.info("인기 상품 랭킹 점수 증가 - 날짜: {}, 상품ID: {}, 추가 수량: {}, 현재 점수: {}", 
                    date, productId, quantity, currentScore + quantity);
        } else {
            zSetOps.add(rankKey, productId.toString(), quantity);
            log.info("인기 상품 랭킹 추가 - 날짜: {}, 상품ID: {}, 수량: {}", date, productId, quantity);
        }
    }

    @Override
    public List<Long> getDailyTopProducts(LocalDate date, int limit) {
        String rankKey = getDailyRankKey(date);
        
        // Sorted Set에서 score 기준 내림차순으로 상위 limit개 조회
        Set<ZSetOperations.TypedTuple<String>> topProducts = 
                redisTemplate.opsForZSet().reverseRangeWithScores(rankKey, 0, limit - 1);
        
        if (topProducts == null || topProducts.isEmpty()) {
            log.info("일별 인기 상품 없음 - 날짜: {}", date);
            return new ArrayList<>();
        }
        
        List<Long> productIds = topProducts.stream()
                .map(tuple -> Long.parseLong(tuple.getValue()))
                .collect(Collectors.toList());
        
        log.info("일별 인기 상품 조회 - 날짜: {}, 상품 수: {}", date, productIds.size());
        return productIds;
    }

    @Override
    public List<Long> getTopProductsByDays(LocalDate startDate, int days, int limit) {
        // 여러 날짜의 Sorted Set을 union하여 합산된 score로 상위 limit개 조회
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            keys.add(getDailyRankKey(date));
        }
        
        // 임시 저장용 키
        String tempKey = "temp:ranking:" + System.currentTimeMillis();
        
        try {
            // 여러 날짜의 Sorted Set을 합산
            redisTemplate.opsForZSet().unionAndStore(
                    keys.get(0), 
                    keys.subList(1, keys.size()), 
                    tempKey);
            
            // 합산된 Sorted Set에서 상위 limit개 조회
            Set<ZSetOperations.TypedTuple<String>> topProducts = 
                    redisTemplate.opsForZSet().reverseRangeWithScores(tempKey, 0, limit - 1);
            
            if (topProducts == null || topProducts.isEmpty()) {
                log.info("기간 내 인기 상품 없음 - 기간: {} ~ {}", startDate, startDate.plusDays(days - 1));
                return new ArrayList<>();
            }
            
            List<Long> productIds = topProducts.stream()
                    .map(tuple -> Long.parseLong(tuple.getValue()))
                    .collect(Collectors.toList());
            
            log.info("기간 내 인기 상품 조회 - 기간: {} ~ {}, 상품 수: {}", 
                    startDate, startDate.plusDays(days - 1), productIds.size());
            return productIds;
        } finally {
            // 임시 키 삭제
            redisTemplate.delete(tempKey);
        }
    }

    private String getDailyRankKey(LocalDate date) {
        return DAILY_RANK_KEY_PREFIX + date.format(DATE_FORMATTER);
    }
}