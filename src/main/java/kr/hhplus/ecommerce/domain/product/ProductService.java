package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.config.exception.CustomException;
import kr.hhplus.ecommerce.config.exception.ErrorCode;
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
    private final ProductEventPublisher productEventPublisher;

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
    public ProductInfo.StockCheckResult reduceStock(OrderCommand.ReduceStock commands) {
        return new ProductInfo.StockCheckResult(commands.orderItems().stream().map(i -> {
            ProductStock productStock = productStockRepository.findByIdWithPessimisticLock(i.productOptionId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            if (productStock.canPurchase(i.quantity())) {
                Long remainingStock = productStock.reduceStock(i.quantity());

                // 추후 리팩토링 필요
                // 재고 차감 성공 이벤트 발행
                productEventPublisher.publish(
                        new ProductEvent.StockDeducted(
                            commands.orderId(),
                            productStock.getId(),
                            i.quantity(),
                            remainingStock
                    )
                );
                return new ProductInfo.StockStatus(
                        productStock.getId(),
                        true,
                        i.quantity(),
                        remainingStock
                );
            } else {
                // 재고 부족 이벤트 발행
                productEventPublisher.publish(
                        new ProductEvent.StockInsufficient(
                                commands.orderId(),
                                productStock.getId(),
                                i.quantity(),
                                productStock.getStock()
                    )
                );
                return new ProductInfo.StockStatus(
                        productStock.getId(),
                        false,
                        i.quantity(),
                        productStock.getStock()
                );
            }
        }).toList());
    }

    @Transactional(readOnly = true)
    public ProductInfo.RankProducts rankProducts(ProductCommand.Products command) {
        log.info("상품 목록 조회 요청 - 상품 ID 목록: {}", command.getProductIds());

        List<ProductInfo.RankProduct> products = new java.util.ArrayList<>();

        // 상품 ID 목록으로 상품 조회
        for (Long productId : command.getProductIds()) {
            try {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                // 상품 정보 변환
                ProductInfo.RankProduct productInfo = ProductInfo.RankProduct.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .productPrice(getProductPrice(product.getId()))
                        .build();

                products.add(productInfo);
            } catch (Exception e) {
                log.warn("상품 조회 실패 - 상품 ID: {}, 오류: {}", productId, e.getMessage());
            }
        }

        log.info("상품 목록 조회 완료 - 상품 수: {}", products.size());
        return ProductInfo.RankProducts.of(products);
    }

    private Long getProductPrice(Long productId) {
        List<ProductStock> stocks = productStockRepository.findByProductId(productId);
        if (stocks.isEmpty()) {
            return 0L;
        }
        return stocks.get(0).getPrice();
    }

}
