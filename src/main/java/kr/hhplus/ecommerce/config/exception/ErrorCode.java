package kr.hhplus.ecommerce.config.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NOT_FOUND("찾을 수 없습니다."),
    BAD_REQUEST("잘못된 요청입니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

}