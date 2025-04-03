package kr.hhplus.ecommerce.interfaces.presentation.controller;

import kr.hhplus.ecommerce.interfaces.presentation.common.ControllerCommonTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StatControllerTest extends ControllerCommonTest {

    @DisplayName("상위 상품 Top5 목록을 가져온다.")
    @Test
    void getRanks() throws Exception {
        // when & then
        mockMvc.perform(
                get("/api/products/top5")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.products[0].productId").value(123))
            .andExpect(jsonPath("$.data.products[0].name").value("상품명1"))
            .andExpect(jsonPath("$.data.products[1].productId").value(456))
            .andExpect(jsonPath("$.data.products[1].name").value("상품명2"));
    }

}