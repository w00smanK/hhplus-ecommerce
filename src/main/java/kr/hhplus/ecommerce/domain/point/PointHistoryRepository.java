package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface PointHistoryRepository {
    PointHistory save(PointHistory pointHistory);

}
