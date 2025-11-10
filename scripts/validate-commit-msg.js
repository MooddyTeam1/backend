#!/usr/bin/env node

// ✅ Node 기반 커밋 메시지 검사/자동 보정 스크립트

const fs = require("fs");

// 사용 가능한 커밋 타입들
const TYPES = [
  "feat",
  "fix",
  "docs",
  "style",
  "refactor",
  "test",
  "build",
  "ci",
  "chore",
  "perf",
  "revert",
];

// type들을 | 로 이어서 정규식 그룹 문자열 생성
const typeGroup = TYPES.join("|");

// ✅ 최종적으로 검사할 규칙
// <type>(<scope>)?: <subject>
// - type: 위 TYPES 중 하나
// - scope: 선택 (소문자/숫자/-/_만)
// - 콜론 뒤에 공백 1칸 + 내용
const VALID_REGEX = new RegExp(`^(${typeGroup})(\\([a-z0-9_-]+\\))?: .+`);

// ❗ 콜론 뒤에 공백이 없는 경우를 찾는 정규식
// 예) feat:테스트 → 매치
//     feat: 테스트 → 매치 X
const NO_SPACE_AFTER_COLON_REGEX = new RegExp(
  `^(${typeGroup}(?:\\([a-z0-9_-]+\\))?):([^\\s])`
);

// 에러 메시지 출력 함수 (형식 위반 시)
function printError(msg) {
  console.error("❌ 잘못된 커밋 메시지 형식입니다.");
  console.error(`   현재 메시지: "${msg}"`);
  console.error("");
  console.error("👉 형식: <type>(<scope>)?: <subject>");
  console.error("   type: " + TYPES.join("|"));
  console.error("   scope: 선택, 소문자/숫자/-/_ 만 가능");
  console.error("");
  console.error("   예시 1) feat: 로그인 API 추가");
  console.error("   예시 2) fix(auth): 토큰 만료 버그 수정");
}

/**
 * ✅ 콜론 뒤에 공백이 없는 메시지를
 *    - feat:로그인 → feat: 로그인
 *    으로 자동 보정해주는 함수
 *
 * @param {string} message   첫 줄 커밋 메시지
 * @param {string|null} filePath  commit-msg 훅에서 넘겨준 파일 경로 (CI에서는 null)
 * @returns {string} 보정된(또는 그대로인) 메시지
 */
function normalizeMessage(message, filePath) {
  // 이미 콜론 뒤에 공백이 있으면 수정할 필요 없음
  if (!NO_SPACE_AFTER_COLON_REGEX.test(message)) {
    return message;
  }

  // 1) 메모리 상에서 문자열 보정
  const fixed = message.replace(NO_SPACE_AFTER_COLON_REGEX, "$1: $2");

  // 2) commit-msg 훅에서 파일 경로가 넘어온 경우, 파일 내용도 같이 수정
  if (filePath) {
    try {
      const content = fs.readFileSync(filePath, "utf8");
      const lines = content.split(/\r?\n/);

      // 첫 줄만 동일한 규칙으로 치환
      lines[0] = lines[0].replace(NO_SPACE_AFTER_COLON_REGEX, "$1: $2");

      fs.writeFileSync(filePath, lines.join("\n"), "utf8");
    } catch (e) {
      // 파일 수정에 실패해도, 최소한 메모리 상 fixed 값으로는 검사 가능하므로
      // 여기서는 그냥 무시하고 진행
    }
  }

  return fixed;
}

// ===== 메인 로직 시작 =====

const args = process.argv.slice(2);
let message = "";
let filePath = null; // 파일 경로 (commit-msg 훅인 경우에만 사용)

// 1) CI에서 사용: --msg "내용"
// 2) 로컬 훅에서 사용: 파일 경로 전달 -> 그 파일 첫 줄 읽기
if (args[0] === "--msg") {
  // ✅ CI 모드: 커밋 메시지를 직접 문자열로 받음
  message = (args[1] || "").trim();
} else if (args[0]) {
  // ✅ 로컬 commit-msg 훅 모드: 첫 번째 인자가 파일 경로
  filePath = args[0];
  try {
    const content = fs.readFileSync(filePath, "utf8");
    message = content.split(/\r?\n/)[0].trim(); // 첫 줄만 검사
  } catch (e) {
    console.error("commit 메시지 파일을 읽을 수 없습니다:", filePath);
    process.exit(1);
  }
} else {
  console.error("커밋 메시지 또는 파일 경로가 제공되지 않았습니다.");
  process.exit(1);
}

if (!message) {
  printError(message);
  process.exit(1);
}

/**
 * ✅ 머지 커밋은 검사하지 않고 무조건 통과
 * 예:
 *   "Merge branch 'feature/xxx' into develop"
 *   "Merge pull request #12 from ..."
 */
if (message.startsWith("Merge ")) {
  process.exit(0);
}

// 필요하면 여기 revert 자동 메시지도 예외로 추가 가능
// if (message.startsWith("Revert \"")) {
//   process.exit(0);
// }

// ✅ 여기서 feat:asdf → feat: asdf 로 자동 보정 시도
message = normalizeMessage(message, filePath);

// ✅ 최종 형식 검사
if (!VALID_REGEX.test(message)) {
  printError(message);
  process.exit(1);
}

// 통과
process.exit(0);
