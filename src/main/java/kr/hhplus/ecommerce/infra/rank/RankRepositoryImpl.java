package kr.hhplus.ecommerce.infra.rank;

import kr.hhplus.ecommerce.domain.rank.RankRepository;
import kr.hhplus.ecommerce.domain.rank.entity.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RankRepositoryImpl implements RankRepository {

    private final RankJpaRepository rankJpaRepository;

    @Override
    public Rank save(Rank rank) {
        return rankJpaRepository.save(rank);
    }

    @Override
    public List<Rank> saveAll(List<Rank> ranks) {
        return rankJpaRepository.saveAll(ranks);
    }

    @Override
    public List<Rank> findTopByRankDate(LocalDate date, int limit) {
        return rankJpaRepository.findTopByRankDate(date, limit);
    }
}
