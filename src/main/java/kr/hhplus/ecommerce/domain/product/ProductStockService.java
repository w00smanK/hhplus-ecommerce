package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.domain.product.dto.ProductStockCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductStockInfo;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductStockService {

    private final ProductStockRepository productStockRepository;

    public void lackStock(ProductStockCommand.OrderProducts command) {
        command.getProducts().forEach(this::lackStock);
    }

    private void lackStock(ProductStockCommand.OrderProduct command) {
        ProductStock productStock = productStockRepository.findByProductId(command.getProductId());
        productStock.lack(command.getQuantity());
    }

    public ProductStockInfo.Stock getStock(Long productId) {
        ProductStock productStock = productStockRepository.findByProductId(productId);
        return ProductStockInfo.Stock.of(productStock.getId(), productStock.getQuantity());
    }
}
