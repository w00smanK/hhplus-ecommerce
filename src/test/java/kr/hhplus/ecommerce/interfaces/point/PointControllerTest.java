package kr.hhplus.ecommerce.interfaces.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.ecommerce.application.point.PointFacade;
import kr.hhplus.ecommerce.application.point.dto.PointCriteria;
import kr.hhplus.ecommerce.application.point.dto.PointResult;
import kr.hhplus.ecommerce.interfaces.ControllerCommonTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PointController.class)
class PointControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointFacade pointFacade;
    @Test
    @DisplayName("사용자 포인트 조회")
    void getUserPoint() throws Exception {
        long userId = 123L;

        PointResult.UserPoint mockResult = PointResult.UserPoint.builder()
                .id(1L)
                .userId(userId)
                .account(1000L)
                .build();

        when(pointFacade.findPoint(any())).thenReturn(mockResult);

        mockMvc.perform(get("/api/v1/points/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.account").value(1000));
    }

    @Test
    @DisplayName("포인트 충전")
    void chargePoint() throws Exception {
        // given
        PointRequest.Charge request = new PointRequest.Charge(123L, 500L);
        String content = objectMapper.writeValueAsString(request);

        PointResult.UserPoint result = PointResult.UserPoint.builder()
                .id(1L)
                .userId(123L)
                .account(1500L)
                .build();

        when(pointFacade.charge(any())).thenReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/points/charge")
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(123))
                .andExpect(jsonPath("$.data.account").value(1500));
    }
}
