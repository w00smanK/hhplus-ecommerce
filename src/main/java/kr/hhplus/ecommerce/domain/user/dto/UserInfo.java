package kr.hhplus.ecommerce.domain.user.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfo {

    @Getter
    @Builder
    @RequiredArgsConstructor(staticName = "of")
    public static  class User{
        private final Long userId;
        private final String username;
    }
}
