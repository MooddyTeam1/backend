-- =====================================================================
-- PostgreSQL 전용 초기 데이터 (data-postgres.sql)
--  - PK는 1000번대부터 수동 지정
--  - 런타임에서 생성되는 ID는 2000번대부터 시작하도록 시퀀스 조정
-- =====================================================================

-- 1. 기존 데이터 정리 ---------------------------------------------------
TRUNCATE TABLE
  project_tag,
  reward_option_values,
  reward_option_groups,
  reward_sets,
  rewards,
  projects,
  maker_wallets,
  makers,
  supporter_profiles,
  platform_wallets,
  users
RESTART IDENTITY CASCADE;

-- 2. 공통 비밀번호 (bcrypt 해시)
-- 비밀번호: "test1234"
-- $2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC

-- 3. users --------------------------------------------------------------
INSERT INTO users (
  id,
  email,
  password,
  name,
  role,
  created_at,
  updated_at,
  last_login_at,
  image_url,
  provider
) VALUES
  (1000, 'user1@test.com',
   '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
   '서포터1', 'USER',
   TIMESTAMP '2024-11-10 09:00:00',
   TIMESTAMP '2024-11-12 10:00:00',
   TIMESTAMP '2024-11-15 08:10:00',
   'https://cdn.moa.dev/avatars/user1.png', 'LOCAL'),

  (1001, 'user2@test.com',
   '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
   '서포터2', 'USER',
   TIMESTAMP '2024-11-10 09:05:00',
   TIMESTAMP '2024-11-12 10:10:00',
   TIMESTAMP '2024-11-15 08:20:00',
   'https://cdn.moa.dev/avatars/user2.png', 'LOCAL'),

  (1002, 'user3@test.com',
   '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
   '서포터3', 'USER',
   TIMESTAMP '2024-11-10 09:10:00',
   TIMESTAMP '2024-11-12 10:20:00',
   TIMESTAMP '2024-11-15 08:30:00',
   'https://cdn.moa.dev/avatars/user3.png', 'LOCAL'),

  (1003, 'maker1@test.com',
   '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
   '메이커1', 'USER',
   TIMESTAMP '2024-11-09 14:00:00',
   TIMESTAMP '2024-11-12 11:00:00',
   TIMESTAMP '2024-11-15 07:50:00',
   'https://cdn.moa.dev/avatars/maker1.png', 'LOCAL'),

  (1004, 'maker2@test.com',
   '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
   '메이커2', 'USER',
   TIMESTAMP '2024-11-09 14:05:00',
   TIMESTAMP '2024-11-12 11:10:00',
   TIMESTAMP '2024-11-15 07:40:00',
   'https://cdn.moa.dev/avatars/maker2.png', 'LOCAL'),

  (1005, 'admin@test.com',
   '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
   '관리자', 'ADMIN',
   TIMESTAMP '2024-11-08 08:30:00',
   TIMESTAMP '2024-11-12 09:00:00',
   TIMESTAMP '2024-11-15 06:30:00',
   'https://cdn.moa.dev/avatars/admin.png', 'LOCAL');

-- 4. supporter_profiles --------------------------------------------------
INSERT INTO supporter_profiles (
  user_id,
  display_name,
  bio,
  image_url,
  phone,
  postal_code,
  created_at,
  updated_at,
  address1,
  address2,
  interests
) VALUES
  (1000, '햇살 서포터',
   '생활형 하드웨어 스타트업을 꾸준히 응원합니다.',
   'https://cdn.moa.dev/avatars/user1.png',
   '010-2000-0001', '06236',
   TIMESTAMP '2024-11-10 09:15:00',
   TIMESTAMP '2024-11-12 10:30:00',
   '서울시 강남구 강남대로 321', '501호',
   '["하드웨어","웰니스"]'),

  (1001, '차분한 분석가',
   '지속 가능성과 실용적인 디자인을 중시합니다.',
   'https://cdn.moa.dev/avatars/user2.png',
   '010-2000-0002', '06102',
   TIMESTAMP '2024-11-10 09:20:00',
   TIMESTAMP '2024-11-12 10:35:00',
   '서울시 강남구 테헤란로 212', '902호',
   '["SaaS","생산성"]'),

  (1002, '주말 백커',
   '아트·테크 협업 프로젝트를 찾아다니는 얼리어답터.',
   'https://cdn.moa.dev/avatars/user3.png',
   '010-2000-0003', '06018',
   TIMESTAMP '2024-11-10 09:25:00',
   TIMESTAMP '2024-11-12 10:40:00',
   '서울시 강남구 도산대로 45', '302호',
   '["아트","가젯"]'),

  (1003, '메이커 겸 서포터',
   '만드는 것도 좋아하고, 멋진 프로젝트도 모아봅니다.',
   'https://cdn.moa.dev/avatars/maker1.png',
   '010-1111-0001', '06055',
   TIMESTAMP '2024-11-09 14:10:00',
   TIMESTAMP '2024-11-12 11:05:00',
   '서울시 강남구 역삼로 99', '7층',
   '["로보틱스","제조"]'),

  (1004, '트레일 메이커',
   '아웃도어 제품을 직접 써보고 피드백합니다.',
   'https://cdn.moa.dev/avatars/maker2.png',
   '010-1111-0002', '04799',
   TIMESTAMP '2024-11-09 14:15:00',
   TIMESTAMP '2024-11-12 11:15:00',
   '서울시 성동구 왕십리로 12', '1204호',
   '["아웃도어","IoT"]'),

  (1005, '플랫폼 지킴이',
   '메인 페이지에 올라갈 만한 프로젝트를 살핍니다.',
   'https://cdn.moa.dev/avatars/admin.png',
   '010-9999-0001', '04524',
   TIMESTAMP '2024-11-08 08:40:00',
   TIMESTAMP '2024-11-12 09:05:00',
   '서울시 중구 을지로 15', '본사 10층',
   '["플랫폼","운영"]');

-- 5. makers --------------------------------------------------------------
INSERT INTO makers (
  id,
  owner_user_id,
  name,
  business_name,
  business_number,
  representative,
  established_at,
  industry_type,
  location,
  product_intro,
  core_competencies,
  image_url,
  contact_email,
  contact_phone,
  tech_stack,
  created_at,
  updated_at
) VALUES
  (1003, 1003,
   '메이커원 스튜디오',
   '메이커원 스튜디오',
   '110-22-334455',
   '박알리스',
   DATE '2021-03-15',
   '스마트 하드웨어',
   '서울시 강남구',
   '일상에서 쓰는 웨어러블 로봇을 연구합니다.',
   '하이브리드 제조, 임베디드 펌웨어, 산업 디자인',
   'https://cdn.moa.dev/makers/maker1.png',
   'maker1@test.com',
   '010-1111-0001',
   '["Spring Boot","Embedded C","PostgreSQL"]',
   TIMESTAMP '2024-11-08 11:00:00',
   TIMESTAMP '2024-11-12 13:45:00'),

  (1004, 1004,
   '트레일랩스',
   'Trail Labs Co.',
   '220-33-778899',
   '최브라이언',
   DATE '2020-05-20',
   '아웃도어 기어',
   '부산시 해운대구',
   '여행자와 하이커를 위한 스마트 액세서리를 만듭니다.',
   '내구성 원단, 저전력 IoT, 민첩한 공급망',
   'https://cdn.moa.dev/makers/maker2.png',
   'maker2@test.com',
   '010-1111-0002',
   '["Kotlin","LoRa","AWS IoT"]',
   TIMESTAMP '2024-11-08 11:10:00',
   TIMESTAMP '2024-11-12 13:50:00');

-- 6. maker_wallets -------------------------------------------------------
INSERT INTO maker_wallets (
  maker_id,
  available_balance,
  pending_balance,
  total_earned,
  total_withdrawn,
  updated_at
) VALUES
  (1003, 0, 0, 0, 0, TIMESTAMP '2024-11-12 13:45:00'),
  (1004, 0, 0, 0, 0, TIMESTAMP '2024-11-12 13:50:00');

-- 7. projects ------------------------------------------------------------
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
  live_end_at,
  canceled_at
) VALUES
  (1200, 1003,
   '오로라 자동조명',
   '하루 리듬에 맞춰 색온도를 조절하는 책상 조명입니다.',
   '## 오로라 자동조명 - 재택 근무자에게 건강한 빛 환경을 제공합니다.',
   2000000,
   DATE '2025-11-13',
   DATE '2026-01-20',
   'TECH',
   'SCHEDULED',
   'APPROVED',
   'NONE',
   TIMESTAMP '2025-11-05 09:00:00',
   TIMESTAMP '2025-11-07 15:00:00',
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/aurora/cover.png',
   '["https://cdn.moa.dev/projects/aurora/gallery-1.png","https://cdn.moa.dev/projects/aurora/gallery-2.png"]',
   TIMESTAMP '2025-11-01 09:00:00',
   TIMESTAMP '2025-11-12 11:00:00',
   TIMESTAMP '2025-12-10 09:00:00',
   TIMESTAMP '2026-01-20 23:59:00',
   NULL),

  (1201, 1003,
   '펄스핏 모듈 밴드',
   '센서를 교체하며 데이터를 맞춤 수집하는 피트니스 밴드입니다.',
   '## 펄스핏 모듈 밴드 - 스타일을 유지하면서도 유의미한 바이오 데이터를 기록합니다.',
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
   TIMESTAMP '2025-12-15 23:59:00',
   NULL),

  (1202, 1003,
   '루멘노트 전자노트',
   '종이 질감을 살리고 배터리 걱정이 없는 전자 필기장입니다.',
   '## 루멘노트 - 종이 같은 필기감과 클라우드 동기화를 동시에 제공합니다.',
   1500000,
   DATE '2025-09-01',
   DATE '2025-10-01',
   'DESIGN',
   'ENDED',
   'APPROVED',
   'SUCCESS',
   TIMESTAMP '2025-08-01 08:00:00',
   TIMESTAMP '2025-08-03 14:00:00',  -- ✅ 콜론(:) 아닌 하이픈(-)
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/lumennote/cover.png',
   '["https://cdn.moa.dev/projects/lumennote/gallery-1.png","https://cdn.moa.dev/projects/lumennote/gallery-2.png"]',
   TIMESTAMP '2025-07-28 11:45:00',
   TIMESTAMP '2025-10-05 12:00:00',
   TIMESTAMP '2025-09-01 10:00:00',
   TIMESTAMP '2025-10-01 23:59:00',
   NULL),

  (1203, 1004,
   '지오트레일 스마트 백팩',
   '태양광 패널과 LTE 트래커를 내장한 여행용 백팩입니다.',
   '## 지오트레일 스마트 백팩 - 밤길에서도 안전하게 이동하고 언제든 위치를 확인하세요.',
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
   TIMESTAMP '2025-11-19 23:59:00',
   NULL);

-- 8. project_tag ---------------------------------------------------------
INSERT INTO project_tag (project_id, tag) VALUES
  (1200, '조명'),
  (1200, '스마트홈'),
  (1201, '피트니스'),
  (1201, '웨어러블'),
  (1202, '생산성'),
  (1202, '페이퍼리스'),
  (1203, '아웃도어'),
  (1203, '여행');

-- 9. platform_wallets (플랫폼 지갑 싱글턴) ------------------------------
INSERT INTO platform_wallets (
  id,
  total_balance,
  total_project_deposit,
  total_maker_payout,
  total_platform_fee,
  created_at,
  updated_at
) VALUES
  (1, 0, 0, 0, 0,
   TIMESTAMP '2024-11-12 09:00:00',
   TIMESTAMP '2024-11-12 09:00:00');

-- 10. rewards ------------------------------------------------------------
INSERT INTO rewards (
  id,
  project_id,
  name,
  description,
  price,
  estimated_delivery_date,
  is_active,
  stock_quantity
) VALUES
  (1300, 1200,
   '오로라 얼리버드 세트',
   '본체 + 디퓨저 + 패브릭 케이블 구성',
   120000,
   DATE '2026-02-15',
   TRUE,
   200),

  (1301, 1201,
   '펄스핏 스타터 패키지',
   '기본 밴드와 센서 카트리지 2종 포함',
   150000,
   DATE '2026-01-20',
   TRUE,
   250),

  (1302, 1202,
   '루멘노트 풀 패키지',
   '전자노트 + 스타일러스 + 폴리오 커버',
   90000,
   DATE '2025-12-05',
   FALSE,
   0),

  (1303, 1203,
   '지오트레일 얼리버드',
   '태양광 패널과 비상 비컨을 포함한 백팩',
   180000,
   DATE '2025-12-15',
   TRUE,
   180);

-- 11. 시퀀스 재시작 (2000번대부터 신규 ID 생성) ------------------------
ALTER SEQUENCE user_id_seq RESTART WITH 2000;
ALTER SEQUENCE maker_id_seq RESTART WITH 2000;
ALTER SEQUENCE project_id_seq RESTART WITH 2000;
ALTER SEQUENCE reward_id_seq RESTART WITH 2000;
ALTER SEQUENCE reward_set_id_seq RESTART WITH 2000;
ALTER SEQUENCE option_group_id_seq RESTART WITH 2000;
ALTER SEQUENCE option_value_id_seq RESTART WITH 2000;


-- =====================================================================
-- maker1( maker_id = 1003 ) 상태별 테스트 프로젝트 4개
-- 작성중(DRAFT/NONE), 심사중(DRAFT/REVIEW), 승인됨(SCHEDULED/APPROVED),
-- 반려됨(DRAFT/REJECTED)
-- ※ data-postgres.sql의 기존 projects INSERT 아래에 이어서 붙이면 됨
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
  live_end_at,
  canceled_at
) VALUES
  -- 1) 작성중: 아직 심사 요청 안 한 초안
  (1204, 1003,
   '메이커1 - 작성중 프로젝트',
   '아직 내용을 채우는 중인 초안 프로젝트입니다.',
   '## 메이커1 - 작성중 프로젝트 - 기본 정보만 입력된 상태입니다.',
   1000000,
   DATE '2026-02-01',
   DATE '2026-03-01',
   'TECH',
   'DRAFT',      -- 작성중
   'NONE',       -- 심사 요청 안 함
   'NONE',
   NULL,
   NULL,
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/maker1-draft/cover.png',
   '["https://cdn.moa.dev/projects/maker1-draft/gallery-1.png"]',
   TIMESTAMP '2025-11-12 10:00:00',
   TIMESTAMP '2025-11-12 10:00:00',
   NULL,
   NULL,
   NULL),

  -- 2) 심사중: 심사 요청 후 관리자 검토 대기
  (1205, 1003,
   '메이커1 - 심사중 프로젝트',
   '심사 요청을 완료하고 관리자의 승인을 기다리는 프로젝트입니다.',
   '## 메이커1 - 심사중 프로젝트 - 심사 결과를 기다리고 있습니다.',
   2000000,
   DATE '2026-03-10',
   DATE '2026-04-10',
   'TECH',
   'DRAFT',      -- 아직 공개 전, 라이프사이클은 작성중 상태로 유지
   'REVIEW',     -- 심사중
   'NONE',
   TIMESTAMP '2025-11-10 09:30:00',  -- request_at
   NULL,
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/maker1-review/cover.png',
   '["https://cdn.moa.dev/projects/maker1-review/gallery-1.png"]',
   TIMESTAMP '2025-11-10 09:00:00',
   TIMESTAMP '2025-11-12 10:10:00',
   NULL,
   NULL,
   NULL),

  -- 3) 승인됨: 승인 완료 + 공개 예정(SCHEDULED)
  (1206, 1003,
   '메이커1 - 승인된 프로젝트(공개예정)',
   '심사를 통과했고 지정된 시작일에 공개될 예정입니다.',
   '## 메이커1 - 승인된 프로젝트(공개예정) - 오픈일까지 사전 마케팅을 진행할 수 있습니다.',
   3000000,
   DATE '2026-05-01',    -- 공개 예정일
   DATE '2026-06-01',
   'DESIGN',
   'SCHEDULED',          -- 공개 예정
   'APPROVED',           -- 승인됨
   'NONE',
   TIMESTAMP '2025-11-08 11:00:00',  -- request_at
   TIMESTAMP '2025-11-09 15:00:00',  -- approved_at
   NULL,
   NULL,
   'https://cdn.moa.dev/projects/maker1-approved/cover.png',
   '["https://cdn.moa.dev/projects/maker1-approved/gallery-1.png"]',
   TIMESTAMP '2025-11-08 10:30:00',
   TIMESTAMP '2025-11-12 10:20:00',
   NULL,  -- live_start_at (아직 라이브 전)
   NULL,  -- live_end_at
   NULL),

  -- 4) 반려됨: 심사에서 반려된 프로젝트
  (1207, 1003,
   '메이커1 - 반려된 프로젝트',
   '심사에서 반려된 프로젝트로, 수정 후 재심사 요청이 필요합니다.',
   '## 메이커1 - 반려된 프로젝트 - 스토리/리스크 설명 보완이 필요합니다.',
   1500000,
   DATE '2026-02-15',
   DATE '2026-03-15',
   'TECH',
   'DRAFT',              -- 여전히 작성중 상태
   'REJECTED',           -- 반려됨
   'NONE',
   TIMESTAMP '2025-11-06 14:00:00',  -- request_at
   NULL,
   TIMESTAMP '2025-11-07 16:30:00',  -- rejected_at
   '프로젝트 리스크 설명과 리워드 배송 계획이 충분하지 않습니다.',
   'https://cdn.moa.dev/projects/maker1-rejected/cover.png',
   '["https://cdn.moa.dev/projects/maker1-rejected/gallery-1.png"]',
   TIMESTAMP '2025-11-06 13:30:00',
   TIMESTAMP '2025-11-12 10:30:00',
   NULL,
   NULL,
   NULL);
INSERT INTO project_wallets (id, project_id, escrow_balance, pending_release, released_total, status, updated_at)
  VALUES (2001, 1201, 0, 0, 0, 'ACTIVE', CURRENT_TIMESTAMP);