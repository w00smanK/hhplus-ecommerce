package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.application.order.OrderFacade;
import kr.hhplus.ecommerce.application.order.dto.OrderCriteria;
import kr.hhplus.ecommerce.application.order.dto.OrderResult;
import kr.hhplus.ecommerce.domain.order.entity.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderFacade orderFacade;

    @Test
    @DisplayName("주문 생성 성공")
    void create() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "userId": 2,
                    "productId": 3,
                    "items": [
                        {
                            "optionId": 301,
                            "quantity": 2
                        }
                    ],
                    "couponId": 5
                }
                """;

        String responseBody = """
                {
                    "orderId": 99,
                    "userId": 2,
                    "status": "CREATED",
                    "totalAmount": 30000,
                    "discountAmount": 5000,
                    "paymentAmount": 25000
                }
                """;

        when(orderFacade.order(any(OrderCriteria.Create.class)))
                .thenReturn(new OrderResult.Create(99L, 2L, OrderStatus.CREATED, 30_000L, 5_000L, 25_000L));

        // Act & Assert
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }
}
