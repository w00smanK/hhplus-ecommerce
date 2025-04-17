package kr.hhplus.ecommerce.infra.point;

import kr.hhplus.ecommerce.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {


}
