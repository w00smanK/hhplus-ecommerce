package kr.hhplus.ecommerce.infra.point;

import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByPointId(Long pointId);

}
