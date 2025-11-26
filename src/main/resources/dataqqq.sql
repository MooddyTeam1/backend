-- =====================================================================
-- dataqqq.sql
--  - 개발용 PostgreSQL 초기 데이터 스크립트
--  - 애플리케이션 실행 시 기존 데이터 전체 초기화 + 기본 계정 생성
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

-- 2. 공통 비밀번호 (bcrypt 해시)
-- 비밀번호: "test1234"
-- $2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC

-- 3. users 기본 계정 생성 ----------------------------------------------
-- ⚠ 중요: users 테이블에 provider 컬럼이 NOT NULL 이기 때문에
--        반드시 provider 값('LOCAL')을 넣어준다.

INSERT INTO users (
  id,
  email,
  password,
  name,
  role,
  provider,          -- ✅ NOT NULL 컬럼
  created_at,
  updated_at,
  last_login_at
) VALUES
  -- 관리자 계정
  (
    1000,
    'admin@moa.test',
    '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
    '관리자',
    'ADMIN',
    'LOCAL',
    now(),
    now(),
    now()
  ),

  -- 서포터 유저 1
  (
    1001,
    'supporter1@moa.test',
    '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
    '서포터 원',
    'USER',
    'LOCAL',
    now(),
    now(),
    now()
  ),

  -- 서포터 유저 2
  (
    1002,
    'supporter2@moa.test',
    '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
    '서포터 투',
    'USER',
    'LOCAL',
    now(),
    now(),
    now()
  ),

  -- 메이커 유저 1
  (
    1003,
    'maker1@moa.test',
    '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
    '메이커원 대표',
    'USER',
    'LOCAL',
    now(),
    now(),
    now()
  ),

  -- 메이커 유저 2
  (
    1004,
    'maker2@moa.test',
    '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC',
    '메이커투 대표',
    'USER',
    'LOCAL',
    now(),
    now(),
    now()
  );

-- =====================================================================
-- 이후에 필요하면 아래에 supporter_profiles, makers, projects 등의
-- 더미 데이터를 점점 추가해 나가면 된다.
-- (지금은 애플리케이션 정상 기동을 우선 목표로 최소 데이터만 넣어둠)
-- =====================================================================
