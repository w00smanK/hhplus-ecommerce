import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    thresholds: {
        http_req_duration: ['p(99)<1000'], // p99 응답시간 1초 미만
        http_req_failed: ['rate<0.01'],    // 에러율 1% 미만
    },
    scenarios: {
        load_test: {
            executor: 'ramping-vus',
            stages: [
                { duration: '1m', target: 50 },   // 1분 동안 50명까지 증가
                { duration: '3m', target: 50 },   // 3분 유지

                { duration: '1m', target: 100 },
                { duration: '3m', target: 100 },

                { duration: '1m', target: 150 },
                { duration: '3m', target: 150 },

                { duration: '1m', target: 200 },
                { duration: '3m', target: 200 },

                { duration: '1m', target: 250 },
                { duration: '3m', target: 250 },
            ],
        },
    }

};

export default function () {
    const res = http.get('http://host.docker.internal:8080/api/v1/rank/products?top=5&days=1');
    check(res, {
        'status is 200': (r) => r.status === 200,
    });
    sleep(1); // 1초 대기 (과도한 요청 방지)
}
