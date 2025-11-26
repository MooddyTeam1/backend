-- =====================================================================
-- PostgreSQL 전용 초기 데이터 (dataqqq.sql)
--  - 개발용 더미 데이터
--  - PK는 1000번대부터 수동 지정 (users/makers/projects 등)
-- =====================================================================

-- 1. 기존 데이터 정리 ---------------------------------------------------
TRUNCATE TABLE
  supporter_bookmarks_project,
  project_tag,
  reward_option_values,
  reward_option_groups,
  reward_sets,
  rewards,
  projects,
  project_wallets,
  maker_wallets,
  makers,
  supporter_profiles,
  platform_wallets,
  users
RESTART IDENTITY CASCADE;

-- 2. 스키마 보정 --------------------------------------------------------
-- 💡 maker 변경 점: 최신 컬럼이 없으면 추가
ALTER TABLE makers
  ADD COLUMN IF NOT EXISTS maker_type varchar(20),
  ADD COLUMN IF NOT EXISTS business_item varchar(100),
  ADD COLUMN IF NOT EXISTS online_sales_registration_no varchar(100);

-- 💡 user 온보딩/알림 설정/프로바이더 컬럼
ALTER TABLE users
  ADD COLUMN IF NOT EXISTS notification_level varchar(20),
  ADD COLUMN IF NOT EXISTS onboarding_status varchar(20),
  ADD COLUMN IF NOT EXISTS provider varchar(20);

-- 기본 provider 값 LOCAL 로 설정 (이미 컬럼이 있어도 괜찮음)
ALTER TABLE users
  ALTER COLUMN provider SET DEFAULT 'LOCAL';

-- 💡 role 체크 제약 재정의 (ADMIN / MAKER / SUPPORTER 허용)
ALTER TABLE users
  DROP CONSTRAINT IF EXISTS users_role_check;

ALTER TABLE users
  ADD CONSTRAINT users_role_check
  CHECK (role IN ('ADMIN', 'MAKER', 'SUPPORTER'));

-- =====================================================================
-- 3. users
--  - 비밀번호는 모두 "test1234" 의 bcrypt 해시 (예시)
--  - provider: LOCAL (일반 회원가입)
-- =====================================================================

INSERT INTO users (
  id,
  email,
  password,
  name,
  role,
  provider,
  created_at,
  updated_at,
  last_login_at,
  notification_level,
  onboarding_status
) VALUES
  -- 관리자
  (1000,
   'admin@moa.dev',
   '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
   'MOA 관리자',
   'ADMIN',
   'LOCAL',
   NOW(), NOW(), NOW(),
   'ALL', 'COMPLETED'),

  -- 메이커1 (메이커원 스튜디오)
  (1001,
   'maker1@test.com',
   '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
   '메이커원',
   'MAKER',
   'LOCAL',
   NOW(), NOW(), NOW(),
   'IMPORTANT', 'COMPLETED'),

  -- 메이커2 (테이스트랩 팀)
  (1002,
   'maker2@test.com',
   '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
   '테이스트랩',
   'MAKER',
   'LOCAL',
   NOW(), NOW(), NOW(),
   'IMPORTANT', 'COMPLETED'),

  -- 서포터1
  (1003,
   'supporter1@test.com',
   '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
   '서포터 김지훈',
   'SUPPORTER',
   'LOCAL',
   NOW(), NOW(), NOW(),
   'ALL', 'COMPLETED');

-- =====================================================================
-- 4. supporter_profiles (서포터용 프로필 간단 더미)
--  - ⚠ supporter_profiles 테이블에 id 컬럼이 없으므로 user_id만 사용
-- =====================================================================

INSERT INTO supporter_profiles (
  user_id,
  nickname,
  profile_image_url,
  bio,
  created_at,
  updated_at
) VALUES
  (1003,
   '지훈',
   'https://picsum.photos/seed/supporter-1003/200/200',
   '하드웨어/테크 제품 좋아하는 얼리어답터 서포터입니다.',
   NOW(), NOW());

-- =====================================================================
-- 5. makers
--  - 메이커 공개 탭 / 메이커 홈에서 사용하는 기본 정보
-- =====================================================================

INSERT INTO makers (
  id,
  user_id,
  maker_type,
  name,
  business_number,
  business_name,
  established_at,
  industry,
  business_item,
  online_sales_registration_no,
  description,
  core_competencies,
  brand_image_url,
  contact_email,
  contact_phone,
  tech_stack,
  address,
  created_at,
  updated_at
) VALUES
  -- 메이커 1003: 메이커원 스튜디오
  (1003,
   1001,                       -- user_id
   'BUSINESS',                 -- maker_type
   '메이커원 스튜디오',        -- 메이커 이름
   '110-22-334455',           -- 사업자번호
   '메이커원 스튜디오',        -- 사업자 상호명
   DATE '2021-03-15',         -- 설립일
   '스마트 하드웨어',           -- 업종(내부 설명용)
   '제조업, 도매 및 소매업',    -- business_item
   '제 0000-서울강남-0000호',   -- 통신판매업 신고번호
   '일상에서 쓰는 웨어러블 로봇을 연구합니다.', -- 소개
   '하이브리드 제조, 임베디드 펌웨어, 산업 디자인', -- 핵심 역량
   'https://picsum.photos/seed/maker-1003/400/400', -- 브랜드 이미지
   'maker1@test.com',
   '010-1111-0001',
   'React, Spring Boot, Embedded, AWS',
   '서울시 강남구',
   NOW(), NOW()),

  -- 메이커 1004: 테이스트키트 팀
  (1004,
   1002,
   'BUSINESS',
   '테이스트키트 랩',
   '220-33-445566',
   '테이스트키트 랩',
   DATE '2020-06-01',
   '식품/푸드테크',
   '제조업, 도소매',
   '제 1111-서울마포-1111호',
   '바쁜 일상 속에서 쉽게 즐기는 프리미엄 간편식을 연구합니다.',
   '레시피 개발, 콜드체인 물류, F&B 브랜딩',
   'https://picsum.photos/seed/maker-1004/400/400',
   'maker2@test.com',
   '010-2222-0002',
   'React, Node.js, AWS',
   '서울시 마포구',
   NOW(), NOW());

-- =====================================================================
-- 6. 플랫폼/메이커 월렛
-- =====================================================================

INSERT INTO platform_wallets (
  id,
  balance,
  created_at,
  updated_at
) VALUES
  (1000, 0, NOW(), NOW());

INSERT INTO maker_wallets (
  id,
  maker_id,
  balance,
  created_at,
  updated_at
) VALUES
  (1000, 1003, 0, NOW(), NOW()),
  (1001, 1004, 0, NOW(), NOW());

-- =====================================================================
-- 7. projects
--  - 홈/공개 프로젝트 목록 및 메이커 프로젝트 탭에서 사용할 기본 더미
--  - ⚠ result_status = 'NONE' 으로 맞춤 (CHECK 제약조건 오류 방지)
-- =====================================================================

INSERT INTO projects (
  id,
  maker_id,
  title,
  summary,
  story_markdown,
  goal_amount,
  start_at,
  end_at,
  category,
  lifecycle_status,
  review_status,
  result_status,
  request_at,
  approved_at,
  rejected_at,
  rejected_reason,
  cover_image_url,
  cover_gallery,
  created_at,
  updated_at,
  live_start_at,
  live_end_at
) VALUES
  -- 예정된 프로젝트 (SCHEDULED)
  (1200, 1003,
   '오로라 자동조명',
   '하루 리듬에 맞춰 색온도를 조절하는 책상 조명입니다.',
   '## 오로라 자동조명' || chr(10) ||
   '재택 근무자에게 건강한 빛 환경을 제공합니다.',
   2000000,
   DATE '2025-11-13',
   DATE '2026-01-20',
   'TECH',                    -- category
   'SCHEDULED',               -- lifecycle_status
   'APPROVED',                -- review_status
   'NONE',                    -- result_status
   TIMESTAMP '2025-11-05 09:00:00',
   TIMESTAMP '2025-11-07 15:00:00',
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/aurora/cover.png',
   '["https://cdn.moa.dev/projects/aurora/gallery-1.png","https://cdn.moa.dev/projects/aurora/gallery-2.png"]',
   TIMESTAMP '2025-11-01 09:00:00',
   TIMESTAMP '2025-11-12 11:00:00',
   TIMESTAMP '2025-12-10 09:00:00',
   TIMESTAMP '2026-01-20 23:59:00'),

  -- 라이브 프로젝트 1 (TECH)
  (1201, 1003,
   '펄스핏 모듈 밴드',
   '센서를 교체하며 데이터를 맞춤 수집하는 피트니스 밴드입니다.',
   '## 펄스핏 모듈 밴드' || chr(10) ||
   '스타일을 유지하면서도 유의미한 바이오 데이터를 기록합니다.',
   3000000,
   DATE '2025-11-01',
   DATE '2025-12-15',
   'TECH',
   'LIVE',
   'APPROVED',
   'NONE',
   TIMESTAMP '2025-10-20 10:00:00',
   TIMESTAMP '2025-10-22 13:00:00',
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/pulsefit/cover.png',
   '["https://cdn.moa.dev/projects/pulsefit/gallery-1.png","https://cdn.moa.dev/projects/pulsefit/gallery-2.png"]',
   TIMESTAMP '2025-10-15 09:30:00',
   TIMESTAMP '2025-11-12 11:10:00',
   TIMESTAMP '2025-11-01 10:00:00',
   TIMESTAMP '2025-12-15 23:59:00'),

  -- 종료된 성공 프로젝트 (DESIGN)
  (1202, 1003,
   '루멘노트 전자노트',
   '종이 질감을 살리고 배터리 걱정이 없는 전자 필기장입니다.',
   '## 루멘노트' || chr(10) ||
   '종이 같은 필기감과 클라우드 동기화를 동시에 제공합니다.',
   1500000,
   DATE '2025-09-01',
   DATE '2025-10-01',
   'DESIGN',
   'ENDED',
   'APPROVED',
   'SUCCESS',
   TIMESTAMP '2025-08-01 08:00:00',
   TIMESTAMP '2025-08-03 14:00:00',
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/lumennote/cover.png',
   '["https://cdn.moa.dev/projects/lumennote/gallery-1.png","https://cdn.moa.dev/projects/lumennote/gallery-2.png"]',
   TIMESTAMP '2025-07-28 11:45:00',
   TIMESTAMP '2025-10-05 12:00:00',
   TIMESTAMP '2025-09-01 10:00:00',
   TIMESTAMP '2025-10-01 23:59:00'),

  -- 라이브 프로젝트 2 (FASHION)
  (1203, 1004,
   '지오트레일 스마트 백팩',
   '태양광 패널과 LTE 트래커를 내장한 여행용 백팩입니다.',
   '## 지오트레일 스마트 백팩' || chr(10) ||
   '밤길에서도 안전하게 이동하고 언제든 위치를 확인하세요.',
   2500000,
   DATE '2025-10-25',
   DATE '2025-11-19',
   'FASHION',
   'LIVE',
   'APPROVED',
   'NONE',
   TIMESTAMP '2025-10-18 11:00:00',
   TIMESTAMP '2025-10-21 09:30:00',
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/geotrail/cover.png',
   '["https://cdn.moa.dev/projects/geotrail/gallery-1.png","https://cdn.moa.dev/projects/geotrail/gallery-2.png"]',
   TIMESTAMP '2025-10-12 10:00:00',
   TIMESTAMP '2025-11-12 11:20:00',
   TIMESTAMP '2025-10-25 09:30:00',
   TIMESTAMP '2025-11-19 23:59:00'),

  -- 라이브 프로젝트 3 (FOOD)
  (1204, 1004,
   '테이스트키트',
   '즉석 조리 키트',
   '## 테이스트키트',
   2000000,
   DATE '2025-11-01',
   DATE '2025-11-10',
   'FOOD',
   'LIVE',
   'APPROVED',
   'NONE',
   TIMESTAMP '2025-10-25 09:00:00',
   TIMESTAMP '2025-10-27 10:00:00',
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/tastekit/cover.png',
   '["https://cdn.moa.dev/projects/tastekit/gallery-1.png"]',
   TIMESTAMP '2025-10-24 09:00:00',
   TIMESTAMP '2025-11-05 09:00:00',
   TIMESTAMP '2025-11-01 09:00:00',
   TIMESTAMP '2025-11-10 23:59:00'),

  -- 라이브 프로젝트 4 (HOME_LIVING)
  (1205, 1003,
   '홈라이트',
   '고속충전 LED 스탠드',
   '## 홈라이트',
   150000,
   DATE '2025-11-01',
   DATE '2025-12-15',
   'HOME_LIVING',
   'LIVE',
   'APPROVED',
   'NONE',
   TIMESTAMP '2025-10-20 09:00:00',
   TIMESTAMP '2025-10-22 10:00:00',
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/homelight/cover.png',
   '["https://cdn.moa.dev/projects/homelight/gallery-1.png"]',
   TIMESTAMP '2025-10-19 09:00:00',
   TIMESTAMP '2025-11-06 09:00:00',
   TIMESTAMP '2025-11-01 09:00:00',
   TIMESTAMP '2025-12-15 23:59:00');

-- =====================================================================
-- 8. 나머지 테이블(rewards 등)은 필요 시 이후에 INSERT 추가
-- =====================================================================

-- 예: rewards / reward_sets / reward_option_groups / reward_option_values /
--     project_tag / supporter_bookmarks_project ...
--     지금은 비워둔 상태로도 애플리케이션이 뜨는 데는 문제가 없도록 구성
