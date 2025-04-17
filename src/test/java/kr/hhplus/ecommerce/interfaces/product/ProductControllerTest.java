package kr.hhplus.ecommerce.interfaces.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.ecommerce.application.product.ProductFacade;
import kr.hhplus.ecommerce.application.product.dto.ProductResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductFacade productFacade;

    @Test
    @DisplayName("전체 상품 목록 조회 - 성공")
    void findAllProducts() throws Exception {
        // given
        ProductResult.ProductDetail product = ProductResult.ProductDetail.builder()
                .productId(1L)
                .brand("Nike")
                .name("나이키 에어포스 1")
                .options(List.of(
                        ProductResult.Option.builder()
                                .optionId(1L)
                                .optionValue("270")
                                .price(129000L)
                                .stock(10L)
                                .build()
                ))
                .build();

        when(productFacade.findAll()).thenReturn(new ProductResult.ProductList(List.of(product)));

        // when & then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.products[0].brand").value("Nike"))
                .andExpect(jsonPath("$.data.products[0].name").value("나이키 에어포스 1"))
                .andExpect(jsonPath("$.data.products[0].options[0].optionValue").value("270"));
    }

    @Test
    @DisplayName("단일 상품 조회 - 성공")
    void findProductById() throws Exception {
        // given
        Long productId = 1L;
        ProductResult.ProductDetail product = ProductResult.ProductDetail.builder()
                .productId(productId)
                .brand("Nike")
                .name("나이키 에어포스 1")
                .options(List.of(
                        ProductResult.Option.builder()
                                .optionId(1L)
                                .optionValue("280")
                                .price(129000L)
                                .stock(5L)
                                .build()
                ))
                .build();

        when(productFacade.findProduct(any())).thenReturn(product);

        // when & then
        mockMvc.perform(get("/api/v1/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(productId))
                .andExpect(jsonPath("$.data.brand").value("Nike"))
                .andExpect(jsonPath("$.data.name").value("나이키 에어포스 1"))
                .andExpect(jsonPath("$.data.options[0].optionValue").value("280"))
                .andExpect(jsonPath("$.data.options[0].stock").value(5));
    }
}
