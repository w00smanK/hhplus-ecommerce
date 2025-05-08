package kr.hhplus.ecommerce.config.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NOT_FOUND("찾을 수 없습니다."),
    BAD_REQUEST("잘못된 요청입니다."),
    PAYMENT_NOT_FOUND("주문에 대한 결제 정보를 찾을 수 없습니다."),
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다."),
    ORDER_ITEM_NOT_FOUND("주문 상품을 찾을 수 없습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

}
