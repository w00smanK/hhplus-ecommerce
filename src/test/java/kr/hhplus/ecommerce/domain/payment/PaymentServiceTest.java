package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import kr.hhplus.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PaymentServiceTest extends MockTestSupport {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @DisplayName("결제를 생성하고 저장한다.")
    @Test
    void pay() {
        // given
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1000L);

        // when
        paymentService.pay(command);

        // then
        verify(paymentRepository).save(any(Payment.class));
    }

//    @DisplayName("최근 N일 이내 완료된 결제의 주문 ID를 반환한다.")
//    @Test
//    void getCompletedOrdersWithinDays() {
//        // given
//        int days = 5;
//        LocalDateTime now = LocalDateTime.now();
//
//        List<Payment> recentPayments = List.of(
//                Payment.builder()
//                        .orderId(1L)
//                        .paymentStatus(PaymentStatus.COMPLETED)
//                        .paidAt(now.minusDays(1))
//                        .build(),
//                Payment.builder()
//                        .orderId(2L)
//                        .paymentStatus(PaymentStatus.COMPLETED)
//                        .paidAt(now.minusDays(2))
//                        .build()
//        );
//
//        when(paymentRepository.findPaymentStatus(anyList(), any(), any()))
//                .thenReturn(recentPayments);
//
//        // when
//        PaymentInfo.Orders result = paymentService.getCompletedOrdersBetweenDays(days);
//
//        // then
//        assertThat(result.getOrderIds())
//                .containsExactlyInAnyOrder(1L, 2L);
//    }
}
