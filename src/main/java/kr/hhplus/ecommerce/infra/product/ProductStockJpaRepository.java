package kr.hhplus.ecommerce.infra.product;

import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {
    List<ProductStock> findAllByProductId(Long productId);
}
