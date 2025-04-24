package kr.hhplus.ecommerce.infra.point;

import jakarta.persistence.LockModeType;
import kr.hhplus.ecommerce.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUserId(Long userId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT b From Point b WHERE b.userId = :userId")
    Optional<Point> findByUserIdWithOptimisticLock(@Param("userId")Long userId);


}
