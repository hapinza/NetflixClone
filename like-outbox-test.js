import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  scenarios: {
    like_test: {
      executor: 'per-vu-iterations',
      vus: 50,
      iterations: 20,
      maxDuration: '2m',
    },
  },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
  const memberId = ((__ITER + __VU) % 190) + 52;
  const movieId = ((__VU - 1) * 20 + __ITER) % 1000 + 1;
  const likeValue = ((__ITER + __VU) % 2) === 0;

  const url = `${BASE_URL}/test/likes/${movieId}?memberId=${memberId}`;

  const payload = JSON.stringify({
    like: likeValue,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.put(url, payload, params);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(0.02);
}