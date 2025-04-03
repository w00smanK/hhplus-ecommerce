package kr.hhplus.ecommerce.interfaces.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleResponse {
    private int code;
    private String message;
}
