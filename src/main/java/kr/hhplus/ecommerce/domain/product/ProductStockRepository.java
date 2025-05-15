package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

public interface ProductStockRepository {

    Optional<ProductStock> findById(Long id);

    Optional<ProductStock> findByIdWithPessimisticLock(Long id);

    List<ProductStock> findByProductId(Long productId);

    ProductStock save(ProductStock productStock);

}
