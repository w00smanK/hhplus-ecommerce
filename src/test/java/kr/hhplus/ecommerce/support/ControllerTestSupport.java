package kr.hhplus.ecommerce.support;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.ecommerce.interfaces.point.PointController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    PointController.class,
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

//    @MockitoBean
//    protected ProductService productService;

//    @MockitoBean
//    protected BalanceFacade balanceFacade;
//
//    @MockitoBean
//    protected OrderFacade orderFacade;
//
//    @MockitoBean
//    protected UserCouponService userCouponService;
}
