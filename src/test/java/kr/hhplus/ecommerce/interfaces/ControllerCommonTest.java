package kr.hhplus.ecommerce.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.ecommerce.application.point.PointFacade;
import kr.hhplus.ecommerce.interfaces.point.PointController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        PointController.class
})
public abstract class ControllerCommonTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PointFacade pointFacade;

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
