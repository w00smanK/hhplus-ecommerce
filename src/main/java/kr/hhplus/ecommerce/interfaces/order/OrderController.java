package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.application.order.dto.OrderResult;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.dto.OrderCommand;
import kr.hhplus.ecommerce.domain.order.dto.OrderInfo;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.dto.ProductCommand;
import kr.hhplus.ecommerce.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<OrderResponse.Create> order(
            @RequestBody OrderRequest.Create request
    ) {
        // 1. 상품 정보 조회
        ProductInfo.ProductDetail product = productService.findProduct(
            new ProductCommand.Find(request.productId())
        );
        
        // 2. 주문 아이템 구성
        List<OrderCommand.OrderItem> orderItems = request.items().stream()
            .flatMap(item -> product.getStocks().stream()
                .filter(option -> item.id().equals(option.getId()))
                .map(option -> new OrderCommand.OrderItem(
                    item.id(), 
                    option.getPrice(),
                        (long) item.quantity().intValue()
                )))
            .toList();
        
        // 3. 주문 생성 (이벤트는 OrderService 내부에서 발행)
        OrderInfo.Create order = orderService.createOrder(
            new OrderCommand.Create(request.userId(), request.couponId(), orderItems)
        );
        
        return ResponseEntity.ok().body(OrderResponse.Create.from(OrderResult.Create.from(order)));
    }
}
