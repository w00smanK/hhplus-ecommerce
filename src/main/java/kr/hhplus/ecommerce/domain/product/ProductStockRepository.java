package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStockRepository {

    Optional<ProductStock> findById(Long optionId);

    List<ProductStock> findByProductId(Long productId);

}
