import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 1,
  iterations: 5,
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const MOVIE_ID = __ENV.MOVIE_ID || '1';

export default function () {
  const url = `${BASE_URL}/analytics/views/${MOVIE_ID}`;

  const res = http.post(url, null, {
    headers: {
      'X-Test-Run': 'smoke-test',
    },
    timeout: '10s',
  });

  check(res, {
    'status is 200 or 202': (r) => r.status === 200 || r.status === 202,
  });
}