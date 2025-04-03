package kr.hhplus.ecommerce.interfaces.presentation.controller;


import kr.hhplus.ecommerce.interfaces.presentation.common.ControllerCommonTest;
import kr.hhplus.ecommerce.interfaces.presentation.request.OrderProductRequest;
import kr.hhplus.ecommerce.interfaces.presentation.request.OrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends ControllerCommonTest {

    @DisplayName("주문 시, 사용자 ID는 필수다.")
    @Test
    void createOrderWithoutUserId() throws Exception {
        // given
        OrderRequest request = OrderRequest.of(
            null,
            1L,
            List.of(OrderProductRequest.of(1L, 1))
        );

        // when & then
        mockMvc.perform(
                post("/api/ orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("사용자 ID는 필수 입니다."));
    }

    @DisplayName("주문 시, 상품목록의 상품 ID는 필수이다.")
    @Test
    void createOrderWithoutProductId() throws Exception {
        // given
        OrderRequest request = OrderRequest.of(
            1L,
            1L,
            List.of(
                OrderProductRequest.of(null, 1)
            )
        );

        // when & then
        mockMvc.perform(
                post("/api/orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("상품 ID는 필수입니다."));
    }


    @DisplayName("주문/결제를 한다.")
    @Test
    void createOrder() throws Exception {
        // given
        OrderRequest request = OrderRequest.of(
            1L,
            1L,
            List.of(
                OrderProductRequest.of(1L, 2)
            )
        );

        // when & then
        mockMvc.perform(
                post("/api/orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"));
    }
}