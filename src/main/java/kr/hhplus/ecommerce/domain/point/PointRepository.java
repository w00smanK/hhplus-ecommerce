package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.entity.Point;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public interface PointRepository {
    Optional<Point> findByUserId(Long userId);

    Optional<Point> findByUserIdWithOptimisticLock(Long userId);
    Point save(Point point);

}
