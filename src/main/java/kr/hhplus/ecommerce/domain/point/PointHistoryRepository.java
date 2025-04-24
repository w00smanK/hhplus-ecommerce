package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PointHistoryRepository {
    PointHistory save(PointHistory history);

    List<PointHistory> findByUserId(Long userId);

}
