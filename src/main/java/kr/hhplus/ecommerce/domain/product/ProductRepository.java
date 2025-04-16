package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository {
    Optional<Product> findById(Long id);

    List<Product> findAll();

}