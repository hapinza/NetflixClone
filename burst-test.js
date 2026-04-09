import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

export const options = {
  scenarios: {
    burst_load: {
      executor: 'constant-arrival-rate',
      rate: 200,
      timeUnit: '1s',
      duration: '30s',
      preAllocatedVUs: 50,
      maxVUs: 300,
    },
  },
  thresholds: {
    checks: ['rate>0.90'],
  },
};

const successCount = new Counter('custom_success_count');
const failCount = new Counter('custom_fail_count');

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const MOVIE_ID = __ENV.MOVIE_ID || '1';

export default function () {
  const url = `${BASE_URL}/analytics/views/${MOVIE_ID}`;

  const res = http.post(url, null, {
    headers: {
      'X-Test-Run': 'burst-test',
    },
    timeout: '10s',
  });

  const ok = check(res, {
    'status is 200 or 202': (r) => r.status === 200 || r.status === 202,
  });

  if (ok) {
    successCount.add(1);
  } else {
    failCount.add(1);
    console.log(`Failed request: status=${res.status}`);
  }

  sleep(0.05);
}