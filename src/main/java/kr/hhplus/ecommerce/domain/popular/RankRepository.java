package kr.hhplus.ecommerce.domain.popular;

import kr.hhplus.ecommerce.domain.popular.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 인기 상품 랭킹 저장소 인터페이스
 */
public interface RankRepository extends JpaRepository<Rank, Long> {

    /**
     * 특정 날짜의 인기 상품 랭킹 조회
     */
    @Query("SELECT r FROM Rank r WHERE r.rankDate = :date ORDER BY r.quantity DESC LIMIT :limit")
    List<Rank> findTopByRankDate(@Param("date") LocalDate date, @Param("limit") int limit);

    /**
     * 여러 날짜의 인기 상품 랭킹 조회
     */
    @Query("SELECT r.productId, SUM(r.quantity) as totalQuantity " +
           "FROM Rank r " +
           "WHERE r.rankDate BETWEEN :startDate AND :endDate " +
           "GROUP BY r.productId " +
           "ORDER BY totalQuantity DESC " +
           "LIMIT :limit")
    List<Object[]> findTopByRankDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") int limit);
}