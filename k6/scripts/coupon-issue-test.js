import http from 'k6/http';
import { check, sleep } from 'k6';

const couponId = 1;

export const options = {
    thresholds: {
        http_req_duration: ['p(99)<1000'],
        http_req_failed: ['rate<0.01'],
    },
    stages: [
        { duration: '10s', target: 100 },
        { duration: '20s', target: 100 },
        { duration: '10s', target: 200 },
        { duration: '20s', target: 200 },
        { duration: '10s', target: 300 },
        { duration: '20s', target: 300 },
        { duration: '10s', target: 0 },
    ]
};

export default function () {
    const userId = __VU;

    const payload = JSON.stringify({
        userId: userId,
        couponId: couponId,
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'coupon_issue' },
        timeout: '60s',
    };

    sleep(Math.random() * 0.1); // 요청 시점을 약간씩 분산

    const res = http.post('http://host.docker.internal:8080/api/v1/coupons/kafka', payload, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    console.log(`사용자 ${userId} 응답 - 상태: ${res.status}`);
}
