package kr.hhplus.ecommerce.domain.popular;

import kr.hhplus.ecommerce.domain.popular.entity.PopularRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 인기 상품 랭킹 저장소 인터페이스
 */
public interface PopularRankRepository extends JpaRepository<PopularRank, Long> {

    /**
     * 특정 날짜의 인기 상품 랭킹 조회
     */
    @Query("SELECT p FROM PopularRank p WHERE p.rankDate = :date ORDER BY p.quantity DESC LIMIT :limit")
    List<PopularRank> findTopByRankDate(@Param("date") LocalDate date, @Param("limit") int limit);

    /**
     * 여러 날짜의 인기 상품 랭킹 조회
     */
    @Query("SELECT p.productId, SUM(p.quantity) as totalQuantity " +
           "FROM PopularRank p " +
           "WHERE p.rankDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.productId " +
           "ORDER BY totalQuantity DESC " +
           "LIMIT :limit")
    List<Object[]> findTopByRankDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") int limit);
}