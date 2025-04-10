package kr.hhplus.ecommerce.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.ecommerce.interfaces.order.OrderController;
import kr.hhplus.ecommerce.interfaces.point.PointController;
import kr.hhplus.ecommerce.interfaces.presentation.controller.*;
import kr.hhplus.ecommerce.interfaces.user.CouponController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    PointController.class,
        CouponController.class,
        OrderController.class,
        StatController.class

})
public abstract class ControllerCommonTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
