package kr.hhplus.ecommerce.domain.rank;

import kr.hhplus.ecommerce.domain.rank.entity.Rank;

import java.time.LocalDate;
import java.util.List;

public interface RankRepository {
    
    /**
     * 랭킹 정보 저장
     */
    Rank save(Rank rank);
    
    /**
     * 랭킹 정보 목록 저장
     */
    List<Rank> saveAll(List<Rank> ranks);
    
    /**
     * 특정 날짜의 인기 상품 랭킹 조회
     */
    List<Rank> findTopByRankDate(LocalDate date, int limit);
}
