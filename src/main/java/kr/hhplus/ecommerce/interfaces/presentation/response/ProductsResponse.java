package kr.hhplus.ecommerce.interfaces.presentation.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductsResponse {

    private List<ProductResponse> products;

    private ProductsResponse(List<ProductResponse> products) {
        this.products = products;
    }

    public static ProductsResponse of(List<ProductResponse> products) {
        return new ProductsResponse(products);
    }
}
