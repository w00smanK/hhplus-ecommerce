package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.config.exception.CustomException;
import kr.hhplus.ecommerce.config.exception.ErrorCode;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.dto.CouponInfo;
import kr.hhplus.ecommerce.domain.coupon.entity.Coupon;
import kr.hhplus.ecommerce.domain.coupon.entity.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;


    @Transactional
    public CouponInfo.CouponStock use(CouponCommand.Use command) {

        if (command.couponId() == null) {
            return CouponInfo.CouponStock.from();
        }
        Coupon coupon = couponRepository.findById(command.couponId())
//        Coupon coupon = couponRepository.findByIdWithLock(command.couponId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        IssuedCoupon issuedCoupon = issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        issuedCoupon.use();

        return CouponInfo.CouponStock.from(coupon, issuedCoupon);
    }

    @Transactional
    public IssuedCoupon issue(CouponCommand.Issue command) {

        Coupon coupon = couponRepository.findById(command.couponId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (coupon.getQuantity() <= 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        coupon.issue();

        return issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));
    }

    @Transactional
    public IssuedCoupon issueWithLock(CouponCommand.Issue command) {

        Coupon coupon = couponRepository.findByIdWithLock(command.couponId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (coupon.getQuantity() <= 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        coupon.issue();

        return issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));
    }

    @Transactional
    public IssuedCoupon save(CouponCommand.Save command) {

        issuedCouponRepository.findByUserIdAndCouponId(command.userId(), command.couponId())
                .ifPresent(coupon -> {
                    throw new CustomException(ErrorCode.BAD_REQUEST);
                });

        return issuedCouponRepository.save(new IssuedCoupon(command.userId(), command.couponId()));
    }
}
