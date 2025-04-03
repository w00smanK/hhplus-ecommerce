package kr.hhplus.ecommerce.interfaces.presentation.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kr.hhplus.ecommerce.interfaces.presentation.request.AccountUpdateRequest;
import kr.hhplus.ecommerce.interfaces.presentation.response.AccountResponse;
import kr.hhplus.ecommerce.interfaces.presentation.response.StatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AccountController {

    /**
     * 1. 잔액 충전 API
     */
    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    @ApiResponse(responseCode = "200", description = "충전 성공")
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    @PostMapping("/{userId}/account")
    public ResponseEntity<StatusResponse<AccountResponse>> updateAccount(@PathVariable("userId") Long id, @Valid @RequestBody AccountUpdateRequest req) {
        AccountResponse data = AccountResponse.of(id, req.getAmount());

        return ResponseEntity.ok(StatusResponse.of(200, "OK", data));
    }

    /**
     * 2. 잔액 조회 API
     */
    @Operation(summary = "잔액 조회", description = "사용자의 현재 잔액을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "잔액 조회 성공")
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    @GetMapping("/{userId}/account")
    public ResponseEntity<StatusResponse<AccountResponse>>getAccount(@PathVariable("userId") Long id) {
        AccountResponse data = AccountResponse.of(id, 1000L);
        return ResponseEntity.ok(StatusResponse.of(200, "OK", data));
    }
}
