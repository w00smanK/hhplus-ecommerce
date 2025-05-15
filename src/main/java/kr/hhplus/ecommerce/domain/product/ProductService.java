package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.config.exception.CustomException;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import kr.hhplus.ecommerce.domain.product.entity.Product;
import kr.hhplus.ecommerce.domain.product.entity.ProductStock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;

    @Transactional
    public ProductInfo.ProductList findAll() {
        List<Product> products = productRepository.findAll();

        List<ProductInfo.ProductDetail> productDetails = products.stream()
                .map(product -> {
                    log.info("product: {}", product);
                    List<ProductStock> productStocks = productStockRepository.findByProductId(product.getId());
                    return ProductInfo.ProductDetail.from(product, productStocks);
                }).toList();

        return ProductInfo.ProductList.of(productDetails);
    }

    @Transactional(readOnly = true)
    public ProductInfo.ProductDetail findProduct(ProductCommand.Find command) {
        log.info("command.getProductId(): {}", command.getProductId());
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        List<ProductStock> productStocks = productStockRepository.findByProductId(command.getProductId());
        log.info("[DEBUG_LOG] productStocks (size={}): {}", productStocks.size(),
                productStocks.stream().map(ProductStock::getId).toList());
        return ProductInfo.ProductDetail.from(product, productStocks);
    }

    @Transactional
    public ProductInfo.ProductDetail findProductByOptionId(ProductCommand.FindByProductOptionId command) {
        ProductStock productStock = productStockRepository.findById(command.getProductOptionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Product product = productRepository.findById(productStock.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        List<ProductStock> productStocks = productStockRepository.findByProductId(product.getId());

        return ProductInfo.ProductDetail.from(product, productStocks);
    }

    @Transactional
//    public ProductInfo.StockCheckResult reduceStock(List<OrderCommand.OrderItem> commands) {
        public ProductInfo.StockCheckResult reduceStock(OrderCommand.OrderItemList commands) {
        return new ProductInfo.StockCheckResult(commands.orderItems().stream().map(i -> {
            ProductStock productStock = productStockRepository.findByIdWithPessimisticLock(i.productOptionId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            if (productStock.canPurchase(i.quantity())) {
                Long remainingStock = productStock.reduceStock(i.quantity());

                return new ProductInfo.StockStatus(
                        productStock.getId(),
                        true,
                        i.quantity(),
                        remainingStock
                );
            } else {
                return new ProductInfo.StockStatus(
                        productStock.getId(),
                        false,
                        i.quantity(),
                        productStock.getStock()
                );
            }
        }).toList());
    }

}
