# 프로젝트 타입 설계 문서

## 개요

이 문서는 MOA 크라우드펀딩 서비스의 프로젝트(Project) 및 리워드(Reward) 관련 Entity와 DTO 타입 구조를 설명합니다.

## 파일 위치

- 타입 정의: `src/features/projects/types.ts`

---

## 목차

1. [Entity (DB 스키마 기반)](#entity-db-스키마-기반)
2. [Request DTO (API 요청)](#request-dto-api-요청)
3. [Response DTO (API 응답)](#response-dto-api-응답)
4. [사용 예시](#사용-예시)

---

## Entity (DB 스키마 기반)

### ProjectEntity

프로젝트 데이터베이스 엔티티 구조입니다.

```typescript
interface ProjectEntity {
  id: ProjectId;                    // 프로젝트 고유 ID
  makerId: MakerId;                 // 메이커 ID (makers.id 참조)
  slug: string;                      // URL-friendly 식별자 (예: "my-awesome-project")
  title: string;                     // 프로젝트 제목
  summary: string;                   // 짧은 요약 설명
  category: string;                  // 카테고리 (테크, 디자인, 푸드 등)
  storyMarkdown: string;             // 마크다운 형식의 스토리
  coverImageUrl: string | null;     // 대표 이미지 URL
  coverGallery: string[];           // 이미지 갤러리 (JSON 배열)
  goalAmount: number;                // 목표 모금액
  startDate: string | null;          // 시작일 (yyyy-mm-dd)
  endDate: string;                   // 종료일 (yyyy-mm-dd)
  projectLifecycleStatus: ProjectLifecycleStatus		// 프로젝트 공개/진행상태
  projectReviewStatus: ProjectReviewStatus;             // 심사/승인상태
  tags: string[];                    // 태그 목록 (JSON 배열)
  createdAt: string;                 // 생성일시 (ISO timestamp)
  updatedAt: string;                 // 수정일시
  approvedAt: string | null;         // 승인 일시
  rejectedAt: string | null;         // 반려 일시
  rejectedReason: string | null;     // 반려 사유
  liveStartedAt: string | null;      // LIVE 시작 일시 (default: startDate 00시00분)
  liveEndedAt: string | null;        // LIVE 종료 일시 (default: endDate 00시00분)
}
```

### RewardEntity

리워드 데이터베이스 엔티티 구조입니다.

```typescript
interface RewardEntity {
  id: RewardId;                      // 리워드 고유 ID
  projectId: ProjectId;              // 프로젝트 ID 참조
  title: string;                     // 리워드 제목
  description: string | null;        // 리워드 설명
  price: number;                     // 리워드 가격
  limitQty: number | null;           // 수량 제한 (null이면 무제한)
  estShippingMonth: string | null;   // 예상 배송 월 (예: "2025-03")
  available: boolean;                // 판매 가능 여부
  optionConfigJson: string | null;   // 옵션 구성 JSON 문자열
  displayOrder: number;              // 표시 순서
  createdAt: string;
  updatedAt: string;
}
```

### ProjectStatus

프로젝트 상태 열거형입니다.

```typescript
type ProjectStatus =
  | "DRAFT"      // 작성 중 (초안)
  | "REVIEW"     // 심사 중
  | "APPROVED"   // 승인됨
  | "SCHEDULED"  // 공개 예정
  | "LIVE"       // 진행 중
  | "ENDED"      // 종료
  | "REJECTED";  // 반려됨
```

---

## Request DTO (API 요청)

### CreateProjectRequestDTO

프로젝트 생성 요청 DTO입니다.

```typescript
interface CreateProjectRequestDTO {
  title: string;
  summary: string;
  category: string;
  storyMarkdown: string;
  coverImageUrl?: string;
  coverGallery?: string[];
  goalAmount: number;
  startDate?: string;                // yyyy-mm-dd
  endDate: string;                   // yyyy-mm-dd
  tags?: string[];
  rewards: CreateRewardRequestDTO[]; // 리워드 목록
}
```

**필수 필드:**
- `title`: 프로젝트 제목
- `summary`: 프로젝트 요약
- `category`: 카테고리
- `storyMarkdown`: 스토리 (마크다운)
- `goalAmount`: 목표 모금액
- `endDate`: 종료일
- `rewards`: 리워드 목록 (최소 1개)

**선택 필드:**
- `coverImageUrl`: 대표 이미지
- `coverGallery`: 이미지 갤러리
- `startDate`: 시작일 (없으면 즉시 시작)
- `tags`: 태그 목록

### UpdateProjectRequestDTO

프로젝트 수정 요청 DTO입니다. 모든 필드가 optional이어서 부분 업데이트를 지원합니다.

```typescript
interface UpdateProjectRequestDTO {
  title?: string;
  summary?: string;
  category?: string;
  storyMarkdown?: string;
  coverImageUrl?: string;
  coverGallery?: string[];
  goalAmount?: number;
  startDate?: string;
  endDate?: string;
  tags?: string[];
  rewards?: CreateRewardRequestDTO[]; // 전체 교체 시에만 제공
}
```

### CreateRewardRequestDTO

리워드 생성/수정 요청 DTO입니다.

```typescript
interface CreateRewardRequestDTO {
  title: string;
  description?: string;
  price: number;
  limitQty?: number;                 // 수량 제한 (없으면 무제한)
  estShippingMonth?: string;         // yyyy-mm 형식
  available?: boolean;                // 기본값: true
  optionConfig?: RewardOptionConfigDTO;
  displayOrder?: number;              // 표시 순서
}
```

### RewardOptionConfigDTO

리워드 옵션 구성 DTO입니다. 색상, 사이즈 등의 옵션을 정의합니다.

```typescript
interface RewardOptionConfigDTO {
  hasOptions: boolean;
  options?: Array<{
    name: string;                     // 옵션명 (예: "색상", "사이즈")
    type: "select" | "text";          // select: 드롭다운, text: 텍스트 입력
    required: boolean;
    choices?: string[];                // type이 "select"일 때 선택지
  }>;
}
```

**예시:**

```typescript
// 색상과 사이즈 옵션이 있는 리워드
{
  hasOptions: true,
  options: [
    {
      name: "색상",
      type: "select",
      required: true,
      choices: ["빨강", "파랑", "초록"]
    },
    {
      name: "사이즈",
      type: "select",
      required: true,
      choices: ["S", "M", "L", "XL"]
    }
  ]
}

// 텍스트 입력 옵션 (예: 각인 문구)
{
  hasOptions: true,
  options: [
    {
      name: "각인 문구",
      type: "text",
      required: false
    }
  ]
}
```

### ChangeProjectStatusRequestDTO

프로젝트 상태 변경 요청 DTO입니다. 심사 제출, 공개 요청 등에 사용됩니다.

```typescript
interface ChangeProjectStatusRequestDTO {
  status: "REVIEW" | "SCHEDULED" | "LIVE";
  scheduledStartDate?: string;        // SCHEDULED일 때 예정일 (yyyy-mm-dd)
}
```

---

## Response DTO (API 응답)

### ProjectDetailResponseDTO

프로젝트 상세 정보 응답 DTO입니다. 계산 필드들을 포함합니다.

```typescript
interface ProjectDetailResponseDTO {
  id: ProjectId;
  makerId: MakerId;
  makerName: string;                 // makers.name
  slug: string;
  title: string;
  summary: string;
  category: string;
  storyMarkdown: string;
  coverImageUrl: string | null;
  coverGallery: string[];
  goalAmount: number;
  raised: number;                    // 누적 모금액 (계산 필드)
  backerCount: number;                // 후원자 수 (계산 필드)
  startDate: string | null;
  endDate: string;
  status: ProjectStatus;
  tags: string[];
  rewards: RewardResponseDTO[];
  createdAt: string;
  updatedAt: string;
  approvedAt: string | null;
  rejectedAt: string | null;
  rejectedReason: string | null;
  liveStartedAt: string | null;
  liveEndedAt: string | null;
  // 계산 필드
  progressPercent: number;            // 진행률 (0-100)
  daysRemaining: number | null;       // 남은 일수 (LIVE일 때만)
  isOwner: boolean;                   // 현재 로그인 유저가 소유자인지
}
```

### ProjectCardResponseDTO

프로젝트 목록에서 사용하는 간소화된 카드 형태 응답 DTO입니다.

```typescript
interface ProjectCardResponseDTO {
  id: ProjectId;
  slug: string;
  title: string;
  summary: string;
  category: string;
  coverImageUrl: string | null;
  goalAmount: number;
  raised: number;
  backerCount: number;
  endDate: string;
  status: ProjectStatus;
  progressPercent: number;
  daysRemaining: number | null;
  makerName: string;
}
```

### RewardResponseDTO

리워드 응답 DTO입니다.

```typescript
interface RewardResponseDTO {
  id: RewardId;
  projectId: ProjectId;
  title: string;
  description: string | null;
  price: number;
  limitQty: number | null;
  remainingQty: number | null;      // 남은 수량 (limitQty - 주문 수량)
  estShippingMonth: string | null;
  available: boolean;
  optionConfig: RewardOptionConfigDTO | null;
  displayOrder: number;
}
```

### ProjectListResponseDTO

페이지네이션된 프로젝트 목록 응답 DTO입니다.

```typescript
interface ProjectListResponseDTO {
  items: ProjectCardResponseDTO[];
  total: number;                     // 전체 항목 수
  page: number;                      // 현재 페이지
  pageSize: number;                  // 페이지 크기
  hasNext: boolean;                  // 다음 페이지 존재 여부
}
```

### ProjectDraftListResponseDTO

메이커 대시보드에서 사용하는 초안 목록 응답 DTO입니다.

```typescript
interface ProjectDraftListResponseDTO {
  items: Array<{
    id: ProjectId;
    title: string;
    summary: string;
    category: string;
    coverImageUrl: string | null;
    goalAmount: number;
    status: ProjectStatus;
    createdAt: string;
    updatedAt: string;
  }>;
  total: number;
}
```

---

## 사용 예시

### 1. 프로젝트 생성

```typescript
import type { CreateProjectRequestDTO } from "@/features/projects/types";

const createProject = async (data: CreateProjectRequestDTO) => {
  const response = await fetch("/api/projects", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return response.json();
};

// 사용 예시
const projectData: CreateProjectRequestDTO = {
  title: "혁신적인 스마트 워치",
  summary: "건강과 생산성을 한 번에! AI 기반 스마트 워치",
  category: "테크",
  storyMarkdown: "# 프로젝트 소개\n\n...",
  coverImageUrl: "https://example.com/cover.jpg",
  coverGallery: ["https://example.com/img1.jpg", "https://example.com/img2.jpg"],
  goalAmount: 50000000,
  endDate: "2025-12-31",
  tags: ["스마트워치", "헬스케어", "AI"],
  rewards: [
    {
      title: "얼리버드 스마트 워치",
      description: "정식 출시 전 특가 혜택",
      price: 89000,
      limitQty: 100,
      estShippingMonth: "2025-06",
      available: true,
      optionConfig: {
        hasOptions: true,
        options: [
          {
            name: "색상",
            type: "select",
            required: true,
            choices: ["블랙", "화이트", "실버"]
          }
        ]
      },
      displayOrder: 1,
    }
  ],
};

await createProject(projectData);
```

### 2. 프로젝트 수정 (부분 업데이트)

```typescript
import type { UpdateProjectRequestDTO } from "@/features/projects/types";

const updateProject = async (id: string, data: UpdateProjectRequestDTO) => {
  const response = await fetch(`/api/projects/${id}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return response.json();
};

// 제목과 요약만 수정
await updateProject("project123", {
  title: "수정된 제목",
  summary: "수정된 요약",
});
```

### 3. 프로젝트 상태 변경 (심사 제출)

```typescript
import type { ChangeProjectStatusRequestDTO } from "@/features/projects/types";

const submitForReview = async (id: string) => {
  const data: ChangeProjectStatusRequestDTO = {
    status: "REVIEW",
  };
  const response = await fetch(`/api/projects/${id}/status`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return response.json();
};

await submitForReview("project123");
```

### 4. 프로젝트 목록 조회

```typescript
import type { ProjectQueryParams, ProjectListResponseDTO } from "@/features/projects/types";

const getProjectList = async (params: ProjectQueryParams): Promise<ProjectListResponseDTO> => {
  const query = new URLSearchParams({
    ...params,
    page: String(params.page ?? 1),
    pageSize: String(params.pageSize ?? 20),
  } as Record<string, string>);
  
  const response = await fetch(`/api/projects?${query}`);
  return response.json();
};

// 테크 카테고리, 인기순 정렬
const projects = await getProjectList({
  category: "테크",
  sortBy: "popular",
  page: 1,
  pageSize: 20,
});
```

### 5. 프로젝트 상세 조회

```typescript
import type { ProjectDetailResponseDTO } from "@/features/projects/types";

const getProjectDetail = async (slug: string): Promise<ProjectDetailResponseDTO> => {
  const response = await fetch(`/api/projects/${slug}`);
  return response.json();
};

const project = await getProjectDetail("my-awesome-project");
console.log(`진행률: ${project.progressPercent}%`);
console.log(`남은 일수: ${project.daysRemaining}일`);
console.log(`내 프로젝트인가: ${project.isOwner}`);
```

---

## 주요 특징

### 1. 부분 업데이트 지원

`UpdateProjectRequestDTO`는 모든 필드가 optional이어서 필요한 필드만 전송할 수 있습니다.

### 2. 계산 필드

Response DTO에는 백엔드에서 계산된 값들이 포함됩니다:
- `raised`: 누적 모금액
- `backerCount`: 후원자 수
- `progressPercent`: 진행률 (0-100)
- `daysRemaining`: 남은 일수
- `remainingQty`: 리워드 남은 수량

### 3. 리워드 옵션 구성

`RewardOptionConfigDTO`를 통해 다양한 옵션을 지원합니다:
- 드롭다운 선택 (색상, 사이즈 등)
- 텍스트 입력 (각인 문구 등)
- 필수/선택 옵션 지정

### 4. 상태 관리

type ProjectLifecycleStatus =
  | 'DRAFT'      // 작성 중, 아직 심사 안 보냄
  | 'SCHEDULED'  // 공개 예정 (start_at > now)
  | 'LIVE'       // 모집 중 (start_at <= now < end_at)
  | 'ENDED';     // 종료 (end_at <= now)

② 심사/승인 상태
// 운영/관리자 기준 심사 상태
type ProjectReviewStatus =
  | 'NONE'       // 아직 심사 요청 전 (DRAFT 단계)
  | 'REVIEW'     // 심사 대기
  | 'APPROVED'   // 심사 통과
  | 'REJECTED';  // 심사 반려

## 🧭 프로젝트 상태 흐름 정리

| 단계 | 상황 설명 | `lifecycleStatus` | `reviewStatus` | 변경 주체 |
|------|-------------|------------------|----------------|------------|
| **1단계. 작성 중** | 사용자가 처음 임시저장 / 작성 중 | `DRAFT` | `NONE` | 사용자 |
| **2단계. 심사 요청** | 사용자가 “심사 요청” 버튼 클릭 | `DRAFT` | `REVIEW` | 사용자 |
| **3단계. 관리자 검토** | 관리자가 프로젝트 심사 중 | `DRAFT` | `REVIEW` | 관리자 |
| **4단계. 승인 완료** | 관리자가 승인 → 심사 통과 | `SCHEDULED` *(startAt 기준 자동 전환)* | `APPROVED` | 관리자 + 시스템 |
| **5단계. 오픈 시작** | `startAt`이 되면 자동 오픈 | `LIVE` | `APPROVED` | 시스템 |
| **6단계. 종료 시점** | `endAt`이 지나면 자동 종료 | `ENDED` | `APPROVED` | 시스템 |
| **반려 시** | 관리자가 반려함 | `DRAFT` | `REJECTED` | 관리자 |
| **재작성 시** | 반려 후 수정 다시 제출 | `DRAFT` | `REVIEW` | 사용자 |


---

## 주의사항

1. **날짜 형식**: 모든 날짜는 `yyyy-mm-dd` 형식의 문자열입니다.
2. **JSON 배열**: `coverGallery`, `tags`는 JSON 배열로 저장되며 프론트엔드에서는 `string[]`로 처리합니다.
3. **옵션 구성**: `optionConfigJson`은 JSON 문자열로 저장되며, Response에서는 파싱된 `RewardOptionConfigDTO`로 제공됩니다.
4. **계산 필드**: `progressPercent`, `daysRemaining` 등은 백엔드에서 계산되어 제공됩니다.
5. **소유자 확인**: `isOwner` 필드는 현재 로그인 유저가 프로젝트 소유자인지 여부를 나타냅니다.

---

## 다음 단계

1. API 서비스 레이어 구현 (`src/features/projects/api/projectService.ts`)
2. React Query 훅 생성 (`src/features/projects/hooks/useProject.ts`)
3. 폼 검증 로직 추가
4. 에러 핸들링 구현

