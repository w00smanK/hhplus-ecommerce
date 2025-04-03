package kr.hhplus.ecommerce.interfaces.presentation.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccountResponse {

    private Long userId;
    private Long account;

    public AccountResponse(Long userId, Long amount) {
        this.userId = userId;
        this.account = amount;
    }
    public static AccountResponse of(Long userId , Long amount) {
        return new AccountResponse(userId , amount);
    }
}
