package kr.hhplus.ecommerce.infra.product;


import kr.hhplus.ecommerce.domain.product.ProductStockRepository;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductStockRepositoryImpl implements ProductStockRepository {

    private final ProductStockJpaRepository productStockJpaRepository;

    @Override
    public Optional<ProductStock> findById(Long optionId) {
        return productStockJpaRepository.findById(optionId);
    }

    @Override
    public List<ProductStock> findByProductId(Long productId) {
        return productStockJpaRepository.findAllByProductId(productId);
    }

    @Override
    public ProductStock save(ProductStock productStock) {
        return productStockJpaRepository.save(productStock);
    }
}
