-- 🔥 상품 (product) 데이터
INSERT INTO product (id, brand, name, registered_at, updated_at)
VALUES
    (1, 'Apple', 'iPhone 15', NOW(), NOW()),
    (2, 'Samsung', 'Galaxy S24', NOW(), NOW());

-- 🔥 옵션 (product_stock) 데이터
INSERT INTO product_stock (id, price, product_id, registered_at, stock, updated_at, option_value)
VALUES
    (101, 1000000, 1, NOW(), 50, NOW(), '128GB'),
    (102, 1200000, 1, NOW(), 30, NOW(), '256GB'),
    (103, 1100000, 2, NOW(), 40, NOW(), '256GB'),
    (104, 1300000, 2, NOW(), 20, NOW(), '512GB');

-- 🔥 사용자 (user) 데이터
INSERT INTO user (user_id, name, registered_at, updated_at)
VALUES
    (1, 'woo', NOW(), NOW());

-- 🔥 쿠폰 (coupon) 데이터
INSERT INTO coupon (coupon_id, discount_price, quantity, registered_at, updated_at, version)
VALUES
    (1, 5000, 100, NOW(), NOW(), 0);

-- 🔥 발급 쿠폰 (issued_coupon) 데이터
INSERT INTO issued_coupon (id, coupon_id, user_id, status, used_at, expired_at)
VALUES
    (1, 1, 1, 0, NULL, DATE_ADD(NOW(), INTERVAL 30 DAY));

-- 🔥 포인트 (point) 데이터
INSERT INTO point (id, user_id, account, registered_at, updated_at, version)
VALUES
    (1, 1, 100000, NOW(), NOW(), 0);

-- 🔥 포인트 이력 (point_history) 데이터
# INSERT INTO point_history (point_history_id, point_id, amount, transaction_type, registered_at, updated_at)
# VALUES
#     (1, 1, 50000, 'CHARGE', NOW(), NOW());
#
# -- 🔥 주문 (order) 데이터
# INSERT INTO `order` (id, user_id, total_amount, discount_amount, payment_amount, registered_at, updated_at, status, issued_coupon_id)
# VALUES
#     (1, 1, 1200000, 5000, 1150000, NOW(), NOW(), 'CREATED', 1);
#
# -- 🔥 주문 상품 (order_item) 데이터
# INSERT INTO order_item (id, order_id, product_option_id, quantity, unit_price, registered_at, updated_at, status)
# VALUES
#     (1, 1, 101, 1, 1000000, NOW(), NOW(), 'CREATED'),
#     (2, 1, 102, 2, 1200000, NOW(), NOW(), 'CREATED');
#
# — 🔥 결제 (payment) 데이터
# INSERT INTO payment (id, order_id, amount, registered_at, updated_at, paid_at, status)
# VALUES
#     (1, 1, 1150000, NOW(), NOW(), NOW(), 'WAITING');