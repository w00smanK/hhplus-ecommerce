package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository {
    Product findById(Long productId);
    List<Product> findByIds(List<Long> productIds);
}
