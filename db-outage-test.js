import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

export const options = {
  scenarios: {
    burst_test: {
      executor: 'ramping-vus',
      stages: [
        { duration: '10s', target: 10 },   // warm-up
        { duration: '5s', target: 100 },   // sudden spike
        { duration: '10s', target: 100 },  // hold
        { duration: '5s', target: 0 },     // ramp down
      ],
      gracefulRampDown: '2s',
    },
  },
  thresholds: {
    checks: ['rate>0.95'],
  },
};

const successCount = new Counter('custom_success_count');
const failCount = new Counter('custom_fail_count');

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const MOVIE_ID = __ENV.MOVIE_ID || '1';

export default function () {
  const url = `${BASE_URL}/analytics/views/${MOVIE_ID}`;

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'X-Test-Run': 'db-outage-test',
    },
    timeout: '10s',
  };

  const res = http.post(url, null, params);

  const ok = check(res, {
    'status is 200 or 202': (r) => r.status === 200 || r.status === 202,
  });

  if (ok) {
    successCount.add(1);
  } else {
    failCount.add(1);
    console.log(`Request failed: status=${res.status}, body=${res.body}`);
  }

  sleep(0.1);
}