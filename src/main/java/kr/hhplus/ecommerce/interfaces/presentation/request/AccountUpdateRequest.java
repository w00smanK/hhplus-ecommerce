package kr.hhplus.ecommerce.interfaces.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccountUpdateRequest {

    @NotNull(message = "잔액은 필수 입니다.")
    @Positive(message = "잔액은 양수여야 합니다.")
    private Long amount;

    private AccountUpdateRequest(Long amount) {
        this.amount = amount;
    }

    public static AccountUpdateRequest of(Long amount) {
        return new AccountUpdateRequest(amount);
    }
}
