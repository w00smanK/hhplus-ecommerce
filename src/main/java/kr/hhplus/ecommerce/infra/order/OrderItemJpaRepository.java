package kr.hhplus.ecommerce.infra.order;


import kr.hhplus.ecommerce.domain.order.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderIdAndProductOptionId(Long orderId, Long productOptionId);

    //    @Query("SELECT oi.productOptionId as productOptionId, SUM(oi.quantity) as totalSaleQuantity " +
//            "FROM OrderItem oi " +
//            "WHERE oi.createdAt >= :startDate " + // 인덱스
//            "GROUP BY oi.productOptionId " +
//            "ORDER BY SUM(oi.quantity) DESC")
    List<BestSellingProjection> findBestSelling(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductOptionId(Long productOptionId);

    interface BestSellingProjection {
        Long getProductOptionId();

        Long getTotalSaleQuantity();
    }
}
