package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStockRepository {

    ProductStock findByProductId(Long productId);
}
