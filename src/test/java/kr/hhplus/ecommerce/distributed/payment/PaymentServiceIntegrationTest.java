package kr.hhplus.ecommerce.distributed.payment;

import kr.hhplus.ecommerce.domain.payment.PaymentRepository;
import kr.hhplus.ecommerce.domain.payment.PaymentService;
import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import kr.hhplus.ecommerce.domain.payment.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("PaymentService 통합테스트")
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    private Payment payment;
    private Long orderId;

    @BeforeEach
    void setUp() {
        orderId = 100L;
        payment = paymentRepository.create(new Payment(orderId, 100000L));
    }


    @Nested
    @DisplayName("결제 처리")
    class Pay {

        @Test
        @DisplayName("전체 금액 결제")
        void payAllAmount() throws Exception {
            PaymentCommand.Pay command = new PaymentCommand.Pay(payment.getId(), 100000L);
            Payment result = paymentService.pay(command);

            Payment actual = paymentRepository.findById(result.getId())
                    .orElseThrow(() -> new Exception("결제 정보를 찾을 수 없습니다."));
            assertThat(actual.getStatus()).isEqualTo(PaymentStatus.PAYED);
            assertThat(actual.getAmount()).isEqualTo(0L);
            assertThat(actual.getPaidAt()).isNotNull();
        }

        @Test
        @DisplayName("일부 금액 결제")
        void payPartialAmount() throws Exception {
            PaymentCommand.Pay command = new PaymentCommand.Pay(payment.getId(), 50000L);
            Payment result = paymentService.pay(command);

            Payment actual = paymentRepository.findById(result.getId())
                    .orElseThrow(() -> new Exception("결제 정보를 찾을 수 없습니다."));
            assertThat(actual.getStatus()).isEqualTo(PaymentStatus.WAITING);
            assertThat(actual.getAmount()).isEqualTo(50000L);
            assertThat(actual.getPaidAt()).isNotNull();
        }
    }
}
