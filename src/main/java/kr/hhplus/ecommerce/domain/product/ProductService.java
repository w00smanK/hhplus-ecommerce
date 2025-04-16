package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.config.exception.Exception;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;

    @Transactional
    public ProductInfo.ProductList findAll() {
        List<Product> products = productRepository.findAll();

        List<ProductInfo.ProductDetail> productDetails = products.stream()
                .map(product -> {
                    List<ProductStock> productStocks = productStockRepository.findByProductId(product.getId());
                    return ProductInfo.ProductDetail.from(product, productStocks);
                }).toList();

        return ProductInfo.ProductList.of(productDetails);
    }

    @Transactional
    public ProductInfo.ProductDetail findProduct(ProductCommand.Find command) {
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND));

        List<ProductStock> productStocks = productStockRepository.findByProductId(command.getProductId());

        return ProductInfo.ProductDetail.from(product, productStocks);
    }

    @Transactional
    public ProductInfo.StockCheckResult reduceStock(List<OrderCommand.OrderItem> commands) {
        List<ProductInfo.StockStatus> stockStatuses = commands.stream()
                .map(item -> {
                    ProductStock stock = productStockRepository.findById(item.productOptionId())
                            .orElseThrow(() -> new Exception(ErrorCode.NOT_FOUND));

                    int remaining = stock.reduceStock(item.quantity());

                    return ProductInfo.StockStatus.builder()
                            .stockId(stock.getId())
                            .isEnough(remaining > 0)
                            .requestQuantity(item.quantity())
                            .remainingQuantity(remaining)
                            .build();
                }).toList();

        return new ProductInfo.StockCheckResult(stockStatuses);
    }

}
