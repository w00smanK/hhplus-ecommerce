package kr.hhplus.ecommerce.infra.rank;

import kr.hhplus.ecommerce.domain.rank.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RankJpaRepository extends JpaRepository<Rank, Long> {

    /**
     * 특정 날짜의 인기 상품 랭킹 조회
     */
    @Query("SELECT r FROM Rank r WHERE r.rankDate = :date ORDER BY r.quantity DESC LIMIT :limit")
    List<Rank> findTopByRankDate(@Param("date") LocalDate date, @Param("limit") int limit);
}
