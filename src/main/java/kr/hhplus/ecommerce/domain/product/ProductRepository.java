package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface ProductRepository {
    Optional<Product> findById(Long id);

    Product save(Product product);

    List<Product> findAll();

}