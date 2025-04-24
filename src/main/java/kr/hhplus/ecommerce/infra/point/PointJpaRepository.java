package kr.hhplus.ecommerce.infra.point;

import kr.hhplus.ecommerce.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUserId(Long userId);

}
