package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.application.order.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderFacade orderFacade;

    @PostMapping
    public ResponseEntity<OrderResponse.Create> order(
            @RequestBody OrderRequest.Create request
    ) {
        return ResponseEntity.ok().body(OrderResponse.Create.from(orderFacade.order(request.toCriteria())));
    }
}
