package kr.hhplus.ecommerce.interfaces.payment;

import kr.hhplus.ecommerce.application.payment.PaymentFacade;
import kr.hhplus.ecommerce.application.payment.dto.PaymentCriteria;
import kr.hhplus.ecommerce.application.payment.dto.PaymentResult;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import kr.hhplus.ecommerce.domain.payment.entity.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentFacade paymentFacade;

    @Test
    @DisplayName("결제 성공")
    void pay() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "orderId": 5,
                    "amount": 15000
                }
                """;

        String responseBody = """
                {
                    "orderId": 5,
                    "orderStatus": "PAYED",
                    "paymentId": 100,
                    "paymentStatus": "PAYED"
                }
                """;

        when(paymentFacade.pay(new PaymentCriteria.Pay(5L, 15_000L)))
                .thenReturn(new PaymentResult.Pay(
                        100L,
                        5L,
                        15_000L,
                        OrderStatus.PAYED,
                        PaymentStatus.PAYED,
                        15_000L,
                        LocalDateTime.now()
                ));

        // Act & Assert
        mockMvc.perform(post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }
}
