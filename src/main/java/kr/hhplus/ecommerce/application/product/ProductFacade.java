package kr.hhplus.ecommerce.application.product;


import kr.hhplus.ecommerce.application.product.dto.ProductResult;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.payment.PaymentService;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.ProductStockService;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductFacade {

//    private static final int RECENT_DAYS = 3;
//    private static final int TOP_LIMIT = 5;

    private final ProductService productService;
    private final ProductStockService productStockService;
    private final PaymentService paymentService;
    private final OrderService orderService;

    public ProductResult.Products getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return ProductResult.Products.of(products.getProducts().stream()
            .map(this::getProduct)
            .toList());
    }
//
//    public ProductResult.Products getPopularProducts() {
//        PaymentInfo.Orders completedOrders = paymentService.getCompletedOrdersBetweenDays(RECENT_DAYS);
//
//        OrderCommand.TopOrders orderProductCommand = OrderCommand.TopOrders.of(completedOrders.getOrderIds(), TOP_LIMIT);
//        OrderInfo.TopPaidProducts topPaidProducts = orderService.getTopPaidProducts(orderProductCommand);
//        ProductInfo.Products products = productService.getProducts(ProductCommand.Products.of(topPaidProducts.getProductIds()));
//
//        return ProductResult.Products.of(products.getProducts().stream()
//            .map(this::getProduct)
//            .toList());
//    }

    @Transactional
    public ProductResult.Product getProduct(Long productId) {
        // 상품 정보
        ProductInfo.ProductDetail productInfo = productService.getProduct(productId);
        return ProductResult.Product.from(productInfo);
    }

}
