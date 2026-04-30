/**
 * 선착순 주문 부하 — k6/tokens.json + 아래 CONFIG
 *
 * 준비: POST /public/test-init, fetch-k6-tokens.ps1 → k6/tokens.json
 * 기본 500 VU. 환경변수 VUS가 있으면 CONFIG.vus보다 우선(예: VUS=100 으로 낮추기).
 * 주문 POST HTTP 타임아웃: 기본 10s (락 대기+서버 처리 합산 고려). http_req_failed 임계값: THRESHOLD_HTTP_REQ_FAILED (기본 15%).
 * 램프업: rampUp 비우면 즉시 VU 도달. 완만히: rampUp: '30s' 등 + RAMP_UP 오버라이드
 *
 * 리포트: 기본 k6/reports/ 타임스탬프 파일. 덮어쓰기: K6_SUMMARY_HTML / K6_ORDER_OUTCOMES_TXT
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import { Counter } from 'k6/metrics';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';

/** 주문 POST 결과 건수 — 이름별로 요약에 그대로 표시 */
const orderCreated = new Counter('order_ok_created');
const order409Stock = new Counter('order_409_stock_shortage');
const order409Lock = new Counter('order_409_optimistic_lock_exhausted');
const order409Project = new Counter('order_409_project_not_funding');
const order409Other = new Counter('order_409_other');
const order400 = new Counter('order_400_bad_request');
const order404Reward = new Counter('order_404_reward_not_found');
const order404Project = new Counter('order_404_project_not_found');
const order404Other = new Counter('order_404_other');
const order401 = new Counter('order_401_unauthorized');
const order5xx = new Counter('order_5xx_server_error');
const orderNet = new Counter('order_0_network_error');
const orderUnexpected = new Counter('order_unexpected');

const CONFIG = {
  baseUrl: 'http://localhost:8080',
  projectId: 2,
  rewardId: 2, // POST /public/test-init 응답 기준
  accessToken: '',
  tokensFile: 'k6/tokens.json',
  vus: 500,
  duration: '30s',
  /** 비우면 즉시 VU 도달 */
  rampUp: '',
  runTestInit: false,
  sleepSec: 0.05,
  /**
   * Grafana k6 대시보드의 testid 드롭다운 필터값.
   * 환경변수 K6_TEST_ID 로 오버라이드 가능. 비우면 태그 없이 실행.
   */
  testId: 'moa-order',
  thresholds: {
    /** 201·409 등은 성공 처리됨. 5xx·status 0만 실패. 동시 부하에 5%는 빡빡해서 기본 완화 */
    httpReqFailedRate: 0.15,
    p95Ms: 10000,
  },
};

function overrideFromEnv() {
  const e = __ENV;
  return {
    baseUrl: (e.BASE_URL || CONFIG.baseUrl).replace(/\/$/, ''),
    authProbePath: e.AUTH_PROBE_PATH || '/profile/me',
    skipAuthProbe: e.SKIP_AUTH_PROBE === '1',
    projectId: e.PROJECT_ID ? parseInt(e.PROJECT_ID, 10) : CONFIG.projectId,
    rewardId: e.REWARD_ID ? parseInt(e.REWARD_ID, 10) : CONFIG.rewardId,
    accessToken: e.ACCESS_TOKEN || CONFIG.accessToken,
    tokensFile: e.TOKENS_FILE || CONFIG.tokensFile,
    vus: e.VUS ? parseInt(e.VUS, 10) : CONFIG.vus,
    duration: e.DURATION || CONFIG.duration,
    rampUp: e.RAMP_UP !== undefined ? e.RAMP_UP : CONFIG.rampUp,
    runTestInit: e.RUN_TEST_INIT === '1' || CONFIG.runTestInit,
    sleepSec: e.SLEEP_SEC !== undefined ? parseFloat(e.SLEEP_SEC) : CONFIG.sleepSec,
    testId: e.K6_TEST_ID || CONFIG.testId,
    thresholds: {
      httpReqFailedRate: e.THRESHOLD_HTTP_REQ_FAILED
        ? parseFloat(e.THRESHOLD_HTTP_REQ_FAILED)
        : CONFIG.thresholds.httpReqFailedRate,
      p95Ms: e.THRESHOLD_P95_MS ? parseInt(e.THRESHOLD_P95_MS, 10) : CONFIG.thresholds.p95Ms,
    },
  };
}

const cfg = overrideFromEnv();

/** open()은 cwd 기준이라, 루트/k6 폴더 등에서 실행해도 찾을 수 있게 후보를 순회한다. */
function loadTokensFromFile() {
  if (cfg.accessToken) return [];
  const candidates = [];
  const push = (p) => {
    if (p && !candidates.includes(p)) candidates.push(p);
  };
  push(cfg.tokensFile);
  push('k6/tokens.json');
  push('./k6/tokens.json');
  push('tokens.json');
  for (const p of candidates) {
    try {
      const raw = open(p);
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed)) return parsed;
      if (parsed && parsed.tokens && Array.isArray(parsed.tokens)) return parsed.tokens;
    } catch (_) {
      /* 다음 경로 */
    }
  }
  return [];
}

const tokensData = new SharedArray('tokens', loadTokensFromFile);

function bearerFromTokenEntry(t) {
  const raw = typeof t === 'string' ? t : t.token || t.accessToken;
  return String(raw == null ? '' : raw).trim();
}

/** ErrorResponse.diagnostic — dev·미처리 예외(5xx)만. 예: "JDBC_CONNECTION_POOL | root=... | ..." */
function parseErrorDiagnostic(body) {
  if (body == null) return '';
  const s = typeof body === 'string' ? body : String(body);
  if (!s || s[0] !== '{') return '';
  try {
    const o = JSON.parse(s);
    return typeof o.diagnostic === 'string' ? o.diagnostic : '';
  } catch (_) {
    return '';
  }
}

function noDiagnosticCategory(r, categoryToken) {
  if (r.status < 500) return true;
  return parseErrorDiagnostic(r.body).indexOf(categoryToken) === -1;
}

function parseErrorJson(body) {
  if (body == null) return null;
  const s = typeof body === 'string' ? body : String(body);
  if (!s || s[0] !== '{') return null;
  try {
    return JSON.parse(s);
  } catch (_) {
    return null;
  }
}

/**
 * 서버 ErrorResponse 기준 분류. 재고 부족·낙관적 락(재시도 초과)은 둘 다 code BUSN-409 이므로 message 로 구분.
 */
function recordOrderCreateMetrics(res) {
  const st = res.status;
  if (st === 201) {
    orderCreated.add(1);
    return;
  }
  if (st === 0) {
    orderNet.add(1);
    return;
  }
  if (st === 401) {
    order401.add(1);
    return;
  }
  if (st >= 500) {
    order5xx.add(1);
    return;
  }

  const j = parseErrorJson(res.body);
  const code = j && typeof j.code === 'string' ? j.code : '';
  const msg = j && typeof j.message === 'string' ? j.message : '';

  if (st === 400) {
    order400.add(1);
    return;
  }
  if (st === 404) {
    if (code === 'RWD-404') order404Reward.add(1);
    else if (code === 'PRJ-404') order404Project.add(1);
    else order404Other.add(1);
    return;
  }
  if (st === 409) {
    if (code === 'PRJ-409') order409Project.add(1);
    else if (msg.indexOf('리워드 재고가 부족') !== -1) order409Stock.add(1);
    else if (msg.indexOf('집중되어') !== -1) order409Lock.add(1);
    else order409Other.add(1);
    return;
  }

  orderUnexpected.add(1);
}

// 기본은 2xx~3xx만 성공 — 409/400/404 등 주문 API의 "정상적인" 4xx는 실패로 잡혀 http_req_failed가 왜곡됨
// setup()의 GET /profile/me(200) 포함
http.setResponseCallback(http.expectedStatuses(200, 201, 400, 404, 409));

if (!cfg.accessToken && tokensData.length === 0) {
  throw new Error(
    '토큰을 찾을 수 없습니다. backend 프로젝트 루트에서 `k6 run k6/k6-load-test.js` 를 실행하거나 ' +
      '`k6` 폴더에서 `k6 run k6-load-test.js` 를 실행하세요. 또는 `TOKENS_FILE`(절대 경로) 또는 `ACCESS_TOKEN` 을 설정하세요. ' +
      '토큰: 서버 기동 후 `fetch-k6-tokens.ps1`(권장, 서버와 동일 JVM) 또는 `export-tokens.ps1` / `export-tokens.cmd`',
  );
}

export const options = {
  vus: cfg.vus,
  duration: cfg.duration,
  /** Grafana k6 대시보드 testid 필터 (label_values(testid) 변수로 드롭다운에 표시됨) */
  tags: cfg.testId ? { testid: cfg.testId } : {},
  thresholds: {
    http_req_failed: [
      { threshold: `rate<${cfg.thresholds.httpReqFailedRate}`, abortOnFail: false },
    ],
    http_req_duration: [
      { threshold: `p(95)<${cfg.thresholds.p95Ms}`, abortOnFail: false },
    ],
  },
};

if (cfg.rampUp) {
  options.stages = [
    { duration: cfg.rampUp, target: cfg.vus },
    { duration: cfg.duration, target: cfg.vus },
  ];
  delete options.vus;
  delete options.duration;
}

export function setup() {
  let pid = String(cfg.projectId);
  let rid = String(cfg.rewardId);

  /** 서버가 같은 JWT_SECRET 으로 토큰을 검증하는지 부하 전에 한 번 확인 (401 스팸 방지) */
  if (!cfg.skipAuthProbe) {
    let probeToken = '';
    if (tokensData.length > 0) {
      probeToken = bearerFromTokenEntry(tokensData[0]);
    } else if (cfg.accessToken) {
      probeToken = String(cfg.accessToken).trim();
    }
    if (probeToken) {
      const probeUrl = `${cfg.baseUrl}${cfg.authProbePath}`;
      const probeRes = http.get(probeUrl, {
        headers: { Authorization: `Bearer ${probeToken}` },
        tags: { name: 'k6-auth-probe' },
      });
      if (probeRes.status === 0) {
        throw new Error(
          `JWT 사전 검증 요청 실패(연결 안 됨): ${probeUrl} — 서버가 ${cfg.baseUrl} 에서 떠 있는지 확인하세요.`,
        );
      }
      if (probeRes.status === 401) {
        throw new Error(
          'JWT가 서버에서 거부되었습니다(401). 서버가 dev 이면 `fetch-k6-tokens.ps1` 로 k6/tokens.json 을 다시 받으세요(실행 중 서버가 직접 발급). ' +
            '또는 JWT_SECRET 을 맞춘 뒤 export-tokens.ps1. (임시: SKIP_AUTH_PROBE=1)',
        );
      }
    }
  }

  if (cfg.runTestInit) {
    const res = http.post(`${cfg.baseUrl}/public/test-init`, null, { tags: { name: 'test-init' } });
    if (res.status === 200 && res.body) {
      const mP = res.body.match(/Project ID:\s*(\d+)/);
      const mR = res.body.match(/Reward ID:\s*(\d+)/);
      if (mP) pid = mP[1];
      if (mR) rid = mR[1];
    }
  }

  return { projectId: pid, rewardId: rid };
}

export default function (data) {
  const pid = data.projectId || String(cfg.projectId);
  const rid = data.rewardId || String(cfg.rewardId);

  let authHeader = '';
  if (tokensData.length > 0) {
    const idx = (__VU - 1) % tokensData.length;
    const t = tokensData[idx];
    const tok = bearerFromTokenEntry(t);
    if (tok) authHeader = `Bearer ${tok}`;
  } else if (cfg.accessToken) {
    authHeader = `Bearer ${String(cfg.accessToken).trim()}`;
  }

  const payload = JSON.stringify({
    projectId: Number(pid),
    receiverName: 'LoadTest',
    receiverPhone: '010-0000-0000',
    addressLine1: '테스트 주소',
    zipCode: '12345',
    items: [{ rewardId: Number(rid), quantity: 1 }],
  });

  const res = http.post(`${cfg.baseUrl}/api/orders`, payload, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: authHeader,
    },
    tags: { name: 'create-order' },
    /** 서버: 락 대기(최대 3s) + TX → 합이 3s를 넘기 쉬워 부하 시 status 0 방지용 */
    timeout: '10s',
  });

  recordOrderCreateMetrics(res);

  const okBusiness =
    res.status === 201 ||
    res.status === 409 ||
    res.status === 400 ||
    res.status === 404;

  check(res, {
    '주문 API 비즈니스 응답(201/409/400/404)': (r) => okBusiness,
    '인증 실패(401) 없음': (r) => r.status !== 401,
    /** prod 등 diagnostic 없으면 5xx만 있어도 여기서는 통과(클라이언트로 구분 불가) */
    '5xx 아님 또는 JDBC 커넥션 풀 진단 없음': (r) => noDiagnosticCategory(r, 'JDBC_CONNECTION_POOL'),
    '5xx 아님 또는 스레드 풀 거부 진단 없음': (r) => noDiagnosticCategory(r, 'THREAD_POOL_REJECTED'),
    '5xx 아님 또는 트랜잭션 리소스 진단 없음': (r) => noDiagnosticCategory(r, 'TRANSACTION_RESOURCE'),
  });

  if (res.status !== 201 && res.status !== 409 && res.status >= 400 && res.status !== 401) {
    console.log(`[${res.status}] ${String(res.body).slice(0, 200)}`);
  }

  /** sleep 0으로 설정했으므로 여기서 대기하지 않고 미친듯이 쏩니다 */
  if (cfg.sleepSec > 0) {
    sleep(cfg.sleepSec);
  }
}

const ORDER_OUTCOME_ROWS = [
  ['order_ok_created', '201 주문 생성 성공'],
  ['order_409_stock_shortage', '409 재고 부족 (리워드 재고 메시지)'],
  ['order_409_optimistic_lock_exhausted', '409 낙관적 락 재시도 초과 (집중 메시지)'],
  ['order_409_project_not_funding', '409 펀딩 아님 (PRJ-409)'],
  ['order_409_other', '409 기타 충돌'],
  ['order_400_bad_request', '400 검증 실패'],
  ['order_404_reward_not_found', '404 리워드 없음'],
  ['order_404_project_not_found', '404 프로젝트 없음'],
  ['order_404_other', '404 기타'],
  ['order_401_unauthorized', '401 인증 실패'],
  ['order_5xx_server_error', '5xx 서버 오류'],
  ['order_0_network_error', '0 네트워크/연결 실패'],
  ['order_unexpected', '기타 예상 밖 상태코드'],
];

function counterCount(data, key) {
  const m = data.metrics[key];
  if (!m || !m.values) return 0;
  const v = m.values.count;
  return typeof v === 'number' ? v : 0;
}

function buildOrderOutcomesText(data) {
  const lines = [
    '=== 주문 POST 결과 건수 (ErrorResponse code/message 기준) ===',
    '(낙관적 락은 재시도 후 성공 시 201으로만 집계됨. 409 "집중"은 재시도 모두 실패한 요청만)',
    '',
  ];
  let sum = 0;
  for (const [key, label] of ORDER_OUTCOME_ROWS) {
    const n = counterCount(data, key);
    sum += n;
    lines.push(`${n}\t${label}`);
  }
  lines.push('');
  lines.push(`합계(위 항목)\t${sum}`);
  return lines.join('\n');
}

/**
 * 파일명용 시각 슬러그 (예: 2026-04-04_015558)
 * TZ 환경변수가 없는 Docker(UTC) 컨테이너에서도 KST(+09:00) 기준으로 출력.
 * 로컬 실행 시에는 시스템 타임존을 그대로 사용.
 */
function reportTimestampSlug() {
  const tzOffset = __ENV.TZ_OFFSET_MIN !== undefined
    ? parseInt(__ENV.TZ_OFFSET_MIN, 10)   // 직접 지정: TZ_OFFSET_MIN=540 (KST)
    : 9 * 60;                              // 기본값: KST (+09:00)
  const d = new Date(Date.now() + tzOffset * 60 * 1000);
  const p = (n) => (n < 10 ? '0' : '') + n;
  return (
    d.getUTCFullYear() +
    '-' +
    p(d.getUTCMonth() + 1) +
    '-' +
    p(d.getUTCDate()) +
    '_' +
    p(d.getUTCHours()) +
    p(d.getUTCMinutes()) +
    p(d.getUTCSeconds())
  );
}

export function handleSummary(data) {
  const ts = reportTimestampSlug();
  const dir = __ENV.K6_REPORT_DIR || 'k6/reports';
  const htmlPath =
    __ENV.K6_SUMMARY_HTML || `${dir}/report-summary-${ts}.html`;
  const textPath =
    __ENV.K6_ORDER_OUTCOMES_TXT || `${dir}/report-order-outcomes-${ts}.txt`;
  return {
    [htmlPath]: htmlReport(data),
    [textPath]: buildOrderOutcomesText(data),
  };
}