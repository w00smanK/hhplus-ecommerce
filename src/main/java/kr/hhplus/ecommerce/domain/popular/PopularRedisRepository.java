package kr.hhplus.ecommerce.domain.popular;

import java.time.LocalDate;
import java.util.List;

/**
 * 인기 상품 Redis 저장소 인터페이스
 */
public interface PopularRedisRepository {

    /**
     * 일별 인기 상품 랭킹 저장
     */
    void addDailyRank(Long productId, Long quantity, LocalDate date);

    /**
     * 일별 인기 상품 랭킹 조회
     */
    List<Long> getDailyTopProducts(LocalDate date, int limit);

    /**
     * 여러 날짜의 인기 상품 랭킹 조회
     */
    List<Long> getTopProductsByDays(LocalDate startDate, int days, int limit);
}