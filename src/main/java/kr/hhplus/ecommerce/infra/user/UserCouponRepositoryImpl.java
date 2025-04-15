//package kr.hhplus.ecommerce.infra.user;
//
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Optional;
//
//@Component
//public class UserCouponRepositoryImpl implements UserCouponRepository {
//
//    @Override
//    public List<UserCouponInfo.UserCouponV1> findUsableCouponsBy(Long userId) {
//        // TODO : 쿠폰이 발급가능, 발급종료 상태여야 하며 사용가능한 사용자 쿠폰만 조회되어야 한다.
//        return List.of();
//    }
//
//    @Override
//    public UserCoupon findUsableCouponBy(Long userId, Long couponId) {
//        return null;
//    }
//
//    @Override
//    public User findUserByUserId(Long userId) {
//        return null;
//    }
//
//    @Override
//    public Coupon findCouponByCouponId(Long couponId) {
//        return null;
//    }
//
//    @Override
//    public UserCoupon save(UserCoupon userCoupon) {
//        return null;
//    }
//
//    @Override
//    public Optional<UserCoupon> findUserCouponByUserIdAndCouponId(Long userId, Long couponId) {
//        return Optional.empty();
//    }
//
//}
