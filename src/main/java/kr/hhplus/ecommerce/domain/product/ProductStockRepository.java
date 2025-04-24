package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface ProductStockRepository {

    Optional<ProductStock> findById(Long optionId);

    Optional<ProductStock> findByIdWithPessimisticLock(Long optionId);

    List<ProductStock> findByProductId(Long productId);

    ProductStock save(ProductStock productStock);

}
