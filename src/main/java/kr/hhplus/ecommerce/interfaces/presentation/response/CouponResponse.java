package kr.hhplus.ecommerce.interfaces.presentation.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CouponResponse {

    private Long id;
    private String name;
    private double discountRate;
    private String expiredAt;

    @Builder
    private CouponResponse(Long id, String name, double discountRate, String expiredAt) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.expiredAt = expiredAt;
    }
}
