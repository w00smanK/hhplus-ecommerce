package kr.hhplus.ecommerce.interfaces.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.ecommerce.application.coupon.CouponFacade;
import kr.hhplus.ecommerce.application.coupon.dto.CouponResult;
import kr.hhplus.ecommerce.domain.coupon.entity.CouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponFacade couponFacade;

    @Test
    @DisplayName("쿠폰 발급 성공")
    void createUserCoupon() throws Exception {
        // given
        CouponRequest.Issue request = new CouponRequest.Issue(1L, 100L);
        String json = objectMapper.writeValueAsString(request);

        CouponResult.Issued mockResult = CouponResult.Issued.builder()
                .id(1L)
                .userId(1L)
                .couponId(100L)
                .status(CouponStatus.ISSUED)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        when(couponFacade.couponFirstIssue(any())).thenReturn(mockResult);

        // when & then
        mockMvc.perform(post("/api/v1/coupons")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.couponId").value(100))
                .andExpect(jsonPath("$.data.status").value("ISSUED"));
    }
}
