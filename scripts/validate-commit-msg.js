#!/usr/bin/env node

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

const typeGroup = TYPES.join("|");
// <type>(<scope>)?: <subject>
const regex = new RegExp(`^(${typeGroup})(\\([a-z0-9_-]+\\))?: .+`);

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

const args = process.argv.slice(2);
let message = "";

// 1) CI에서 사용: --msg "내용"
// 2) 로컬 훅에서 사용: 파일 경로 전달 -> 그 파일 첫 줄 읽기
if (args[0] === "--msg") {
  message = (args[1] || "").trim();
} else if (args[0]) {
  const fs = require("fs");
  try {
    const content = fs.readFileSync(args[0], "utf8");
    message = content.split(/\r?\n/)[0].trim(); // 첫 줄만 검사
  } catch (e) {
    console.error("commit 메시지 파일을 읽을 수 없습니다:", args[0]);
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

// 필요하면 여기에 revert 자동 메시지도 예외로 추가 가능
// if (message.startsWith("Revert \"")) {
//   process.exit(0);
// }

if (!regex.test(message)) {
  printError(message);
  process.exit(1);
}

// 통과
process.exit(0);
