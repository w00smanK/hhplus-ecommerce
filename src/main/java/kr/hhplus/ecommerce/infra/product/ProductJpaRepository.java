package kr.hhplus.ecommerce.infra.product;

import kr.hhplus.ecommerce.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

}
