package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.domain.point.entity.Point;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository {
    Optional<Point> findBy(Long userId);

    Point save(Point point);
}
