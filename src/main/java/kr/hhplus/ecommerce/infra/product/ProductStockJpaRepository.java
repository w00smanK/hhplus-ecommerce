package kr.hhplus.ecommerce.infra.product;

import jakarta.persistence.LockModeType;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {
    List<ProductStock> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ProductStock ps WHERE ps.id = :id")
    Optional<ProductStock> findByIdWithPessimisticLock(@Param("id") Long id);
}
