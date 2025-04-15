//package kr.hhplus.ecommerce.interfaces.point;
//
//public record PointRequest() {
//
//    public record Find() {
//        public static PointCriteria.Find toCriteria(Long userId) {
//            return new PointCriteria.Find(userId);
//        }
//    }
//
//    public record Charge(
//            @Min(value = 0, message = "0원 이상만 충전 가능합니다.")
//            Long amount
//    ) {
//        public PointCriteria.Charge toCriteria(Long userId) {
//            return new PointCriteria.Charge(userId, amount);
//        }
//    }
//}