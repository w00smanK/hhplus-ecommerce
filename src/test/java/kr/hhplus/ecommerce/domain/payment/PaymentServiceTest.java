package kr.hhplus.ecommerce.domain.payment;


import kr.hhplus.ecommerce.config.exception.CustomException;
import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.payment.dto.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.entity.Payment;
import kr.hhplus.ecommerce.domain.payment.entity.PaymentStatus;
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
@DisplayName("PaymentService")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment PAYMENT;

    @BeforeEach
    void setUp() {
        PAYMENT = new Payment(100L, 100_000L);
    }

    @Nested
    @DisplayName("조회")
    class Find {

        @Test
        @DisplayName("성공")
        void success() throws java.lang.Exception {
            when(paymentRepository.findByOrderId(anyLong())).thenReturn(Optional.of(PAYMENT));

            Payment result = paymentService.findPayment(new PaymentCommand.FindOrder(anyLong()));

            verify(paymentRepository).findByOrderId(anyLong());
            assertThat(result.getOrderId()).isEqualTo(100L);
            assertThat(result.getAmount()).isEqualTo(100_000L);
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.WAITING);
            assertThat(result.getPaidAt()).isNull();
        }

        @Test
        @DisplayName("실패 - 없음")
        void fail_notFound() {
            when(paymentRepository.findByOrderId(anyLong())).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class,
                    () -> paymentService.findPayment(new PaymentCommand.FindOrder(anyLong())));

            verify(paymentRepository).findByOrderId(anyLong());
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("처리")
    class Pay {

        @Test
        @DisplayName("성공 - 전액")
        void full() throws java.lang.Exception {
            when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(PAYMENT));

            Payment result = paymentService.pay(new PaymentCommand.Pay(anyLong(), 100_000L));

            verify(paymentRepository).findById(anyLong());
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAYED);
            assertThat(result.getAmount()).isZero();
            assertThat(result.getPaidAt()).isNotNull();
        }

        @Test
        @DisplayName("성공 - 일부")
        void partial() throws java.lang.Exception {
            when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(PAYMENT));

            Payment result = paymentService.pay(new PaymentCommand.Pay(anyLong(), 50_000L));

            verify(paymentRepository).findById(anyLong());
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.WAITING);
            assertThat(result.getAmount()).isEqualTo(50_000L);
            assertThat(result.getPaidAt()).isNotNull();
        }

        @Test
        @DisplayName("실패 - 없음")
        void fail_notFound() {
            when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class,
                    () -> paymentService.pay(new PaymentCommand.Pay(anyLong(), 10_000L)));

            verify(paymentRepository).findById(anyLong());
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }
}
