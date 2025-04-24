package kr.hhplus.ecommerce.interfaces.payment;

import kr.hhplus.ecommerce.application.payment.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentFacade paymentFacade;

    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @RequestBody PaymentRequest request
    ) throws Exception {
        return ResponseEntity.ok()
                .body(PaymentResponse.from(paymentFacade.pay(request.toCriteria())));
    }
}
