package kr.hhplus.ecommerce.infra.point;

import kr.hhplus.ecommerce.domain.point.PointHistoryRepository;
import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    @Override
    public PointHistory save(PointHistory pointHistory) {
        return null;
    }
}
