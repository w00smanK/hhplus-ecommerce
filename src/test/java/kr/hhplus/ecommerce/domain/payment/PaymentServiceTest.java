package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import kr.hhplus.ecommerce.domain.payment.entity.PaymentStatus;
import kr.hhplus.ecommerce.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 단위 테스트")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Long USER_ID;
    private Long PAYMENT_ID;
    private Long ORDER_ID;
    private Payment PAYMENT;

    @BeforeEach
    void setUp() {
        USER_ID = 1L;
        PAYMENT_ID = 1L;
        ORDER_ID = 100L;

        PAYMENT = Payment.builder()
                .id(PAYMENT_ID)
                .orderId(ORDER_ID)
                .status(PaymentStatus.PENDING)
                .build();
    }

    @Nested
    @DisplayName("결제 조회")
    class Find {

        @Test
        @DisplayName("결제 조회 성공")
        void success() {
            Payment payed = PAYMENT.pay(1000L);

            when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(payed));

            Payment result = paymentService.findPayment(new PaymentCommand.Find(PAYMENT_ID));

            verify(paymentRepository, times(1)).findById(PAYMENT_ID);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(PAYMENT_ID);
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAYED);
        }

        @Test
        @DisplayName("결제 조회 실패 - 존재하지 않음")
        void notFound() {
            when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.empty());

            Exception ex = assertThrows(Exception.class,
                    () -> paymentService.findPayment(new PaymentCommand.Find(PAYMENT_ID)));

            verify(paymentRepository).findById(PAYMENT_ID);
            assertThat(ex.getMessage()).isEqualTo(ErrorCode.NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("결제 처리")
    class Pay {

        @Test
        @DisplayName("결제 성공")
        void success() {
            when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(PAYMENT));

            Payment result = paymentService.pay(new PaymentCommand.Pay(PAYMENT_ID, 10000L));

            verify(paymentRepository, times(1)).findById(PAYMENT_ID);
            assertThat(result.getId()).isEqualTo(PAYMENT_ID);
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAYED);
            assertThat(result.getAmount()).isEqualTo(10000L);
            assertThat(result.getPaidAt()).isNotNull();
        }

        @Test
        @DisplayName("결제 실패 - 존재하지 않음")
        void notFound() {
            when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.empty());

            Exception ex = assertThrows(Exception.class,
                    () -> paymentService.pay(new PaymentCommand.Pay(PAYMENT_ID, 10000L)));

            verify(paymentRepository).findById(PAYMENT_ID);
            assertThat(ex.getMessage()).isEqualTo(ErrorCode.NOT_FOUND.getMessage());
        }
    }
}
