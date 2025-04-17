package kr.hhplus.ecommerce.infra.point;

import kr.hhplus.ecommerce.domain.point.PointHistoryRepository;
import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryRepository;
    @Override
    public PointHistory save(PointHistory history) {
        pointHistoryRepository.save(history);
        return history;
    }

}
