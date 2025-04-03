package kr.hhplus.ecommerce.interfaces.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductResponse {

    private Long productId;
    private String name;
    private String brand;
    private int price;
    private int stock;
    private List<StockQuantity> stockQuantities;

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public static class StockQuantity {
        private String size;
        private int quantity;
    }
    public ProductResponse toSummary() {
        return ProductResponse.builder()
                .id(this.productId)
                .name(this.name)
                .build();
    }
    @Builder
    private ProductResponse(Long id, String name, String brand,int price, int stock, List<StockQuantity> stockQuantities) {
        this.productId = id;
        this.name = name;
        this.brand=brand;
        this.price = price;
        this.stock = stock;
        this.stockQuantities = stockQuantities;
    }


}
