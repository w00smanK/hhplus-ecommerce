package kr.hhplus.ecommerce.interfaces.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.ecommerce.interfaces.common.StatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Point", description = "포인트 관련 API")
public interface PointApi {

    @Operation(summary = "포인트 조회", description = "사용자의 현재 포인트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{userId}")
    StatusResponse<PointResponse.UserPoint> getUserPoint(
            @Parameter(description = "유저 ID", example = "1") @PathVariable("userId") Long userId
    );

    @Operation(summary = "포인트 충전", description = "포인트를 충전합니다.")
    @ApiResponse(responseCode = "200", description = "충전 성공")
    @PostMapping("/charge")
    StatusResponse<PointResponse.UserPoint> chargePoint(@RequestBody PointRequest.Charge request);
}
