package kr.hhplus.ecommerce.domain.rank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RankRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String DAILY_RANK_KEY_PREFIX = "rank:daily:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 일별 판매량 랭킹 추가
     */
    public void addDailyRank(Long productId, Long quantity, LocalDate date) {
        String key = DAILY_RANK_KEY_PREFIX + date.format(DATE_FORMATTER);
        redisTemplate.opsForZSet().incrementScore(key, productId.toString(), quantity);
        log.debug("일별 판매량 랭킹 추가 - 상품: {}, 수량: {}, 날짜: {}", productId, quantity, date);
    }

    /**
     * 특정 날짜의 상위 판매 상품 조회
     */
    public List<Long> getTopProductsByDays(LocalDate date, int days, int limit) {
        String key = DAILY_RANK_KEY_PREFIX + date.format(DATE_FORMATTER);
        Set<String> productIds = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }

        return productIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}