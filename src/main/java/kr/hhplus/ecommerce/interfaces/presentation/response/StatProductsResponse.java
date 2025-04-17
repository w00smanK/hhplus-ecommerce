package kr.hhplus.ecommerce.interfaces.presentation.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class StatProductsResponse {

    private List<ProductResponse> products;

    private StatProductsResponse(List<ProductResponse> products) {
        this.products = products;
    }

    public static StatProductsResponse of(List<ProductResponse> products) {
        return new StatProductsResponse(products);
    }
}
