//package kr.hhplus.ecommerce.interfaces.point;
//
//
//@Builder
//public record PointResponse() {
//
//    public record UserPoint(
//            Long userId,
//            Long amount
//    ) {
//        public static UserPoint fromResult(PointResult.UserPoint result) {
//            return new UserPoint(result.userId(), result.point());
//        }
//    }
//}