package kr.hhplus.ecommerce.interfaces.point;

import kr.hhplus.ecommerce.interfaces.ControllerCommonTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PointControllerTest extends ControllerCommonTest {

    @Test
    void updateAccount() {
    }

    @Test
    void getAccount() throws Exception {
        mockMvc.perform(
                        get("/api/users/{userId}/account", 1L)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.account").value(1000L));
    }
}