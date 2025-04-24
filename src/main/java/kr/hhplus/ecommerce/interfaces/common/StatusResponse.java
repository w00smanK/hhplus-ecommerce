package kr.hhplus.ecommerce.interfaces.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatusResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    public static <T> StatusResponse<T> of(int status, String message, T data) {
        return StatusResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
