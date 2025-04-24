package kr.hhplus.ecommerce.interfaces.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@AllArgsConstructor(staticName = "of")
public class StatusResponse<T> {
    @JsonInclude(NON_NULL)
    private final T data;
    private int code;
    private String message;
}