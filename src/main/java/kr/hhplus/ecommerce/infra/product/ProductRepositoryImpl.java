package kr.hhplus.ecommerce.infra.product;


import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.product.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductRepositoryImpl implements ProductRepository {


    @Override
    public Product findById(Long productId) {
        return null;
    }

    @Override
    public List<Product> findByIds(List<Long> productIds) {
        return null;
    }
}
