package kr.hhplus.ecommerce.domain.payment;

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
        payment = paymentRepository.save(new Payment(orderId, 100000L));
    }

    @Test
    @DisplayName("결제 조회")
    void findPayment() {
        PaymentCommand.FindOrder command = new PaymentCommand.FindOrder(orderId);
        Payment result = paymentService.findPayment(command);

        Payment actual = paymentRepository.findById(result.getId()).get();
        assertThat(actual.getOrderId()).isEqualTo(100L);
        assertThat(actual.getAmount()).isEqualTo(100000L);
        assertThat(actual.getStatus()).isEqualTo(PaymentStatus.WAITING);
        assertThat(actual.getPaidAt()).isNull();
    }

    @Nested
    @DisplayName("결제 처리")
    class Pay {

        @Test
        @DisplayName("전체 금액 결제")
        void payAllAmount() {
            PaymentCommand.Pay command = new PaymentCommand.Pay(payment.getId(), 100000L);
            Payment result = paymentService.pay(command);

            Payment actual = paymentRepository.findById(result.getId()).get();
            assertThat(actual.getStatus()).isEqualTo(PaymentStatus.PAYED);
            assertThat(actual.getAmount()).isEqualTo(0L);
            assertThat(actual.getPaidAt()).isNotNull();
        }

        @Test
        @DisplayName("일부 금액 결제")
        void payPartialAmount() {
            PaymentCommand.Pay command = new PaymentCommand.Pay(payment.getId(), 50000L);
            Payment result = paymentService.pay(command);

            Payment actual = paymentRepository.findById(result.getId()).get();
            assertThat(actual.getStatus()).isEqualTo(PaymentStatus.WAITING);
            assertThat(actual.getAmount()).isEqualTo(50000L);
            assertThat(actual.getPaidAt()).isNotNull();
        }
    }
}
