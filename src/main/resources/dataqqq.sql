-- 개발용 H2 시드 데이터 (통계/정산/지갑/발표 시연용 풀 세트)
-- 용도: /api/admin/statistics*, 정산/지갑 화면, 스케줄러 로컬 스모크 테스트
-- 대표 계정:
--   서포터: user_id 1000 / user1@test.com / test1234
--   메이커(플래그십): user_id 1003 / maker1@test.com / test1234
--   관리자: user_id 1005 / admin@test.com / test1234
-- 범위 요약:
--   서포터: 1000~1005 기본, 1010~1011 신규, 1020~1024 추가
--   메이커: 1003 플래그십, 전시용 1004~1014
--   프로젝트: 플래그십/기본 1200~1215, 전시용 1216~1233
-- 주문/결제/환불: 플래그십 대량(1400~1500대), 전시용 고액 1~2건씩, 환불 포함
-- 수수료: PG 5% + 플랫폼 10% 적용(net=85%)
-- 정산/지갑: 플래그십은 부분/완전 정산, 전시용은 PENDING 최소셋
-- 리셋: TRUNCATE 후 insert, 시퀀스/IDENTITY RESTART 포함
-- 비밀번호 "test1234"의 bcrypt 해시: $2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC

BEGIN;
TRUNCATE TABLE
  order_items,
  orders,
  payments,
  refunds,
  platform_wallet_transactions,
  settlements,
  maker_wallets,
  platform_wallets,
  project_tag,
  rewards,
  projects,
  makers,
  supporter_profiles,
  users
RESTART IDENTITY CASCADE;


INSERT INTO users (id, email, password, name, role, onboarding_status, created_at, updated_at, last_login_at, image_url, provider) VALUES
  (1000, 'user1@test.com',  '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '서포터1', 'USER', 'NOT_STARTED',
   TIMESTAMP '2024-11-10 09:00:00', TIMESTAMP '2024-11-12 10:00:00', TIMESTAMP '2024-11-15 08:10:00',
   'https://cdn.moa.dev/avatars/user1.png', 'LOCAL'),
  (1001, 'user2@test.com',  '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '서포터2', 'USER', 'NOT_STARTED',
   TIMESTAMP '2024-11-10 09:05:00', TIMESTAMP '2024-11-12 10:10:00', TIMESTAMP '2024-11-15 08:20:00',
   'https://cdn.moa.dev/avatars/user2.png', 'LOCAL'),
  (1002, 'user3@test.com',  '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '서포터3', 'USER', 'NOT_STARTED',
   TIMESTAMP '2024-11-10 09:10:00', TIMESTAMP '2024-11-12 10:20:00', TIMESTAMP '2024-11-15 08:30:00',
   'https://cdn.moa.dev/avatars/user3.png', 'LOCAL'),
  (1003, 'maker1@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '메이커1', 'USER', 'NOT_STARTED',
   TIMESTAMP '2024-11-09 14:00:00', TIMESTAMP '2024-11-12 11:00:00', TIMESTAMP '2024-11-15 07:50:00',
   'https://cdn.moa.dev/avatars/maker1.png', 'LOCAL'),
  (1004, 'maker2@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '메이커2', 'USER', 'NOT_STARTED',
   TIMESTAMP '2024-11-09 14:05:00', TIMESTAMP '2024-11-12 11:10:00', TIMESTAMP '2024-11-15 07:40:00',
   'https://cdn.moa.dev/avatars/maker2.png', 'LOCAL'),
  (1005, 'admin@test.com',  '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '관리자', 'ADMIN', 'NOT_STARTED',
   TIMESTAMP '2024-11-08 08:30:00', TIMESTAMP '2024-11-12 09:00:00', TIMESTAMP '2024-11-15 06:30:00',
   'https://cdn.moa.dev/avatars/admin.png', 'LOCAL'),
  -- 신규 서포터 (2025-11 가입) : 신규/리텐션 지표용
  (1010, 'newuser1@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '신규서포터1', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-11-02 09:00:00', TIMESTAMP '2025-11-02 09:00:00', TIMESTAMP '2025-11-02 09:10:00',
   'https://cdn.moa.dev/avatars/new1.png', 'LOCAL'),
  (1011, 'newuser2@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '신규서포터2', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-11-03 10:00:00', TIMESTAMP '2025-11-03 10:00:00', TIMESTAMP '2025-11-03 10:05:00',
   'https://cdn.moa.dev/avatars/new2.png', 'LOCAL');

INSERT INTO supporter_profiles (user_id, display_name, bio, image_url, phone, postal_code, created_at, updated_at, address1, address2, interests) VALUES
  (1000, '햇살 서포터', '생활형 하드웨어 스타트업을 꾸준히 응원합니다.', 'https://cdn.moa.dev/avatars/user1.png',
   '010-2000-0001', '06236', TIMESTAMP '2024-11-10 09:15:00', TIMESTAMP '2024-11-12 10:30:00',
   '서울시 강남구 강남대로 321', '501호', '["하드웨어","웰니스"]'),
  (1001, '차분한 분석가', '지속 가능성과 실용적인 디자인을 중시합니다.', 'https://cdn.moa.dev/avatars/user2.png',
   '010-2000-0002', '06102', TIMESTAMP '2024-11-10 09:20:00', TIMESTAMP '2024-11-12 10:35:00',
   '서울시 강남구 테헤란로 212', '902호', '["SaaS","생산성"]'),
  (1002, '주말 백커', '아트·테크 협업 프로젝트를 찾아다니는 얼리어답터.', 'https://cdn.moa.dev/avatars/user3.png',
   '010-2000-0003', '06018', TIMESTAMP '2024-11-10 09:25:00', TIMESTAMP '2024-11-12 10:40:00',
   '서울시 강남구 도산대로 45', '302호', '["아트","가젯"]'),
  (1003, '메이커 겸 서포터', '만드는 것도 좋아하고, 멋진 프로젝트도 모아봅니다.', 'https://cdn.moa.dev/avatars/maker1.png',
   '010-1111-0001', '06055', TIMESTAMP '2024-11-09 14:10:00', TIMESTAMP '2024-11-12 11:05:00',
   '서울시 강남구 역삼로 99', '7층', '["로보틱스","제조"]'),
  (1004, '트레일 메이커', '아웃도어 제품을 직접 써보고 피드백합니다.', 'https://cdn.moa.dev/avatars/maker2.png',
   '010-1111-0002', '04799', TIMESTAMP '2024-11-09 14:15:00', TIMESTAMP '2024-11-12 11:15:00',
   '서울시 성동구 왕십리로 12', '1204호', '["아웃도어","IoT"]'),
  (1005, '플랫폼 지킴이', '메인 페이지에 올라갈 만한 프로젝트를 살핍니다.', 'https://cdn.moa.dev/avatars/admin.png',
   '010-9999-0001', '04524', TIMESTAMP '2024-11-08 08:40:00', TIMESTAMP '2024-11-12 09:05:00',
   '서울시 중구 을지로 15', '본사 10층', '["플랫폼","운영"]'),
  (1010, '신규 백커 A', '테크/디자인 프로젝트에 관심', 'https://cdn.moa.dev/avatars/new1.png',
   '010-3000-0001', '06000', TIMESTAMP '2025-11-02 09:05:00', TIMESTAMP '2025-11-02 09:10:00',
   '서울시 강남구 언주로 100', '1층', '["TECH","DESIGN"]'),
  (1011, '신규 백커 B', '푸드/홈리빙 관심', 'https://cdn.moa.dev/avatars/new2.png',
   '010-3000-0002', '06110', TIMESTAMP '2025-11-03 10:05:00', TIMESTAMP '2025-11-03 10:10:00',
   '서울시 강남구 테헤란로 50', '12층', '["FOOD","HOME_LIVING"]');

-- 메이커 & 지갑
INSERT INTO makers (id, owner_user_id, maker_type, name, business_name, business_number, representative, established_at, industry_type, location, product_intro, core_competencies, image_url, contact_email, contact_phone, tech_stack, created_at, updated_at) VALUES
  (1003, 1003, 'BUSINESS', '메이커원 스튜디오', '메이커원 스튜디오', '110-22-334455', '박알리스', DATE '2021-03-15',
   '스마트 하드웨어', '서울시 강남구', '일상에서 쓰는 웨어러블 로봇을 연구합니다.',
   '하이브리드 제조, 임베디드 펌웨어, 산업 디자인',
   'https://cdn.moa.dev/makers/maker1.png', 'maker1@test.com', '010-1111-0001',
   '["Spring Boot","Embedded C","PostgreSQL"]',
   TIMESTAMP '2024-11-08 11:00:00', TIMESTAMP '2024-11-12 13:45:00'),
  (1004, 1004, 'BUSINESS', '트레일랩스', 'Trail Labs Co.', '220-33-778899', '최브라이언', DATE '2020-05-20',
   '아웃도어 기어', '부산시 해운대구', '여행자와 하이커를 위한 스마트 액세서리를 만듭니다.',
   '내구성 원단, 저전력 IoT, 민첩한 공급망',
   'https://cdn.moa.dev/makers/maker2.png', 'maker2@test.com', '010-1111-0002',
   '["Kotlin","LoRa","AWS IoT"]',
   TIMESTAMP '2024-11-08 11:10:00', TIMESTAMP '2024-11-12 13:50:00');

-- 메이커 지갑 (정산 대기 금액 반영, 부분정산 포함)
-- 1003: 1202(완료 229,500) + 1201(1차 지급 150,000, 잔액 232,500 대기) + 1205(대기 187,000)
--      → available=379,500 / pending=419,500 / total_earned=799,000
-- 1004: 1203(대기 306,000) + 1204(대기 42,500)
INSERT INTO maker_wallets (id, maker_id, available_balance, pending_balance, total_earned, total_withdrawn, updated_at) VALUES
  (1, 1003, 379500, 419500, 799000, 0, TIMESTAMP '2025-11-12 13:45:00'),
  (2, 1004, 0,      348500, 348500, 0, TIMESTAMP '2025-11-12 13:50:00');

-- 플랫폼 지갑 싱글턴
-- 플랫폼 지갑 (수수료만 반영: 총 99,000 = 10% 수수료 - 환불 18,000)
INSERT INTO platform_wallets (id, total_balance, total_project_deposit, total_maker_payout, total_platform_fee, created_at, updated_at)
VALUES (1, 99000, 1170000, 0, 99000, TIMESTAMP '2024-11-12 09:00:00', TIMESTAMP '2024-11-12 09:00:00');



-- 프로젝트 (기존 + 위험/기회 샘플)
INSERT INTO projects (id, maker_id, title, summary, story_markdown, goal_amount, start_at, end_at,
                      category, lifecycle_status, review_status, result_status,
                      request_at, approved_at, rejected_at, rejected_reason,
                      cover_image_url, cover_gallery, created_at, updated_at,
                      live_start_at, live_end_at)
VALUES
  (1200, 1003, '오로라 자동조명',
   '하루 리듬에 맞춰 색온도를 조절하는 책상 조명입니다.',
   '## 오로라 자동조명' || chr(10) || '재택 근무자에게 건강한 빛 환경을 제공합니다.',
   2000000, DATE '2025-11-13', DATE '2026-01-20',
   'TECH', 'SCHEDULED', 'APPROVED', 'NONE',
   TIMESTAMP '2025-11-05 09:00:00', TIMESTAMP '2025-11-07 15:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/aurora/cover.png',
   '["https://cdn.moa.dev/projects/aurora/gallery-1.png","https://cdn.moa.dev/projects/aurora/gallery-2.png"]',
   TIMESTAMP '2025-11-01 09:00:00', TIMESTAMP '2025-11-12 11:00:00',
   TIMESTAMP '2025-12-10 09:00:00', TIMESTAMP '2026-01-20 23:59:00'),

  (1201, 1003, '펄스핏 모듈 밴드',
   '센서를 교체하며 데이터를 맞춤 수집하는 피트니스 밴드입니다.',
   '## 펄스핏 모듈 밴드' || chr(10) || '스타일을 유지하면서도 유의미한 바이오 데이터를 기록합니다.',
   3000000, DATE '2025-11-01', DATE '2025-12-15',
   'TECH', 'LIVE', 'APPROVED', 'NONE',
   TIMESTAMP '2025-10-20 10:00:00', TIMESTAMP '2025-10-22 13:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/pulsefit/cover.png',
   '["https://cdn.moa.dev/projects/pulsefit/gallery-1.png","https://cdn.moa.dev/projects/pulsefit/gallery-2.png"]',
   TIMESTAMP '2025-10-15 09:30:00', TIMESTAMP '2025-11-12 11:10:00',
   TIMESTAMP '2025-11-01 10:00:00', TIMESTAMP '2025-12-15 23:59:00'),

  (1202, 1003, '루멘노트 전자노트',
   '종이 질감을 살리고 배터리 걱정이 없는 전자 필기장입니다.',
   '## 루멘노트' || chr(10) || '종이 같은 필기감과 클라우드 동기화를 동시에 제공합니다.',
   1500000, DATE '2025-09-01', DATE '2025-10-01',
   'DESIGN', 'ENDED', 'APPROVED', 'SUCCESS',
   TIMESTAMP '2025-08-01 08:00:00', TIMESTAMP '2025-08-03 14:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/lumennote/cover.png',
   '["https://cdn.moa.dev/projects/lumennote/gallery-1.png","https://cdn.moa.dev/projects/lumennote/gallery-2.png"]',
   TIMESTAMP '2025-07-28 11:45:00', TIMESTAMP '2025-10-05 12:00:00',
   TIMESTAMP '2025-09-01 10:00:00', TIMESTAMP '2025-10-01 23:59:00'),

  (1203, 1004, '지오트레일 스마트 백팩',
   '태양광 패널과 LTE 트래커를 내장한 여행용 백팩입니다.',
   '## 지오트레일 스마트 백팩' || chr(10) || '밤길에서도 안전하게 이동하고 언제든 위치를 확인하세요.',
   2500000, DATE '2025-10-25', DATE '2025-11-19',
   'FASHION', 'LIVE', 'APPROVED', 'NONE',
   TIMESTAMP '2025-10-18 11:00:00', TIMESTAMP '2025-10-21 09:30:00', NULL, NULL,
   'https://cdn.moa.dev/projects/geotrail/cover.png',
   '["https://cdn.moa.dev/projects/geotrail/gallery-1.png","https://cdn.moa.dev/projects/geotrail/gallery-2.png"]',
   TIMESTAMP '2025-10-12 10:00:00', TIMESTAMP '2025-11-12 11:20:00',
   TIMESTAMP '2025-10-25 09:30:00', TIMESTAMP '2025-11-19 23:59:00'),

  -- 위험 샘플: FOOD, 목표 2,000,000, 종료까지 5일 남은 가정(달성률 낮음)
  (1204, 1004, '테이스트키트', '즉석 조리 키트', '## 테이스트키트', 2000000, DATE '2025-11-01', DATE '2025-11-10',
   'FOOD', 'LIVE', 'APPROVED', 'NONE',
   TIMESTAMP '2025-10-25 09:00:00', TIMESTAMP '2025-10-27 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/tastekit/cover.png',
   '["https://cdn.moa.dev/projects/tastekit/gallery-1.png"]',
   TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-11-05 09:00:00',
   TIMESTAMP '2025-11-01 09:00:00', TIMESTAMP '2025-11-10 23:59:00'),

  -- 기회 샘플: HOME_LIVING, 목표 150,000, 남은일 > 14, 달성률 높음
  (1205, 1003, '홈라이트', '고속충전 LED 스탠드', '## 홈라이트', 150000, DATE '2025-11-01', DATE '2025-12-15',
   'HOME_LIVING', 'LIVE', 'APPROVED', 'NONE',
   TIMESTAMP '2025-10-20 09:00:00', TIMESTAMP '2025-10-22 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/homelight/cover.png',
   '["https://cdn.moa.dev/projects/homelight/gallery-1.png"]',
   TIMESTAMP '2025-10-19 09:00:00', TIMESTAMP '2025-11-06 09:00:00',
   TIMESTAMP '2025-11-01 09:00:00', TIMESTAMP '2025-12-15 23:59:00');

-- 프로젝트 지갑 (정산 스케줄러/지갑 화면용 샘플 금액)
-- 1201은 부분정산 반영: 총 주문액 450k 중 수수료/환불 제외한 net 382.5k 기준으로 1차 150k 지급 → released=150k, pending_release=232.5k, escrow에는 남은 net(232.5k)을 표시
INSERT INTO project_wallets (id, escrow_balance, pending_release, released_total, status, updated_at, project_id) VALUES
  (1, 232500, 232500, 150000, 'ACTIVE', TIMESTAMP '2025-11-12 11:10:00', 1201),
  (2, 270000, 0, 0, 'ACTIVE', TIMESTAMP '2025-10-05 12:00:00', 1202),
  (3, 180000, 0, 0, 'ACTIVE', TIMESTAMP '2025-11-12 11:20:00', 1203),
  (4, 50000,  0, 0, 'ACTIVE', TIMESTAMP '2025-11-05 09:00:00', 1204),
  (5, 220000, 0, 0, 'ACTIVE', TIMESTAMP '2025-11-06 09:00:00', 1205);

INSERT INTO project_tag (project_id, tag) VALUES
  (1200, '조명'), (1200, '스마트홈'),
  (1201, '피트니스'), (1201, '웨어러블'),
  (1202, '생산성'), (1202, '페이퍼리스'),
  (1203, '아웃도어'), (1203, '여행'),
  (1204, '푸드'), (1204, '간편식'),
  (1205, '홈리빙'), (1205, '충전');

-- 리워드
INSERT INTO rewards (id, project_id, name, description, price, estimated_delivery_date, is_active, stock_quantity, version) VALUES
  (1300, 1200, '오로라 얼리버드 세트', '본체 + 디퓨저 + 패브릭 케이블 구성', 120000, DATE '2026-02-15', TRUE, 200, 0),
  (1301, 1201, '펄스핏 스타터 패키지', '기본 밴드와 센서 카트리지 2종 포함', 150000, DATE '2026-01-20', TRUE, 250, 0),
  (1302, 1202, '루멘노트 풀 패키지', '전자노트 + 스타일러스 + 폴리오 커버', 90000, DATE '2025-12-05', FALSE, 0, 0),
  (1303, 1203, '지오트레일 얼리버드', '태양광 패널과 비상 비컨을 포함한 백팩', 180000, DATE '2025-12-15', TRUE, 180, 0),
  (1304, 1204, '테이스트키트 얼리버드', '즉석 조리 키트 샘플', 50000, DATE '2025-12-30', TRUE, 100, 0),
  (1305, 1205, '홈라이트 얼리버드', '고속충전 LED 스탠드', 220000, DATE '2026-01-10', TRUE, 150, 0);

-- 주문
INSERT INTO orders (id, order_id, order_name, user_id, project_id, status, total_amount,
                    receiver_name, receiver_phone, address_line1, address_line2, zip_code,
                    delivery_status, created_at, updated_at)
VALUES
  (1400, 'ORD-20251101-AAA', '펄스핏 스타터 패키지', 1000, 1201, 'PAID', 150000,
   '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236',
   'NONE', TIMESTAMP '2025-11-01 10:15:00', TIMESTAMP '2025-11-01 10:20:00'),
  (1401, 'ORD-20251102-BBB', '펄스핏 스타터 패키지', 1001, 1201, 'PAID', 300000,
   '서포터2', '010-2000-0002', '서울시 강남구 테헤란로 212', '902호', '06102',
   'NONE', TIMESTAMP '2025-11-02 11:30:00', TIMESTAMP '2025-11-02 11:35:00'),
  (1402, 'ORD-20251103-CCC', '지오트레일 얼리버드', 1002, 1203, 'CANCELED', 180000,
   '서포터3', '010-2000-0003', '서울시 강남구 도산대로 45', '302호', '06018',
   'NONE', TIMESTAMP '2025-11-03 12:00:00', TIMESTAMP '2025-11-03 12:10:00'),
  (1403, 'ORD-20251005-DDD', '루멘노트 풀 패키지', 1000, 1202, 'PAID', 270000,
   '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236',
   'NONE', TIMESTAMP '2025-10-05 09:15:00', TIMESTAMP '2025-10-05 09:20:00'),
  (1404, 'ORD-20251104-EEE', '지오트레일 얼리버드', 1010, 1203, 'PAID', 180000,
   '신규서포터1', '010-3000-0001', '서울시 강남구 언주로 100', '1층', '06000',
   'NONE', TIMESTAMP '2025-11-04 09:10:00', TIMESTAMP '2025-11-04 09:12:00'),
  (1405, 'ORD-20251105-FFF', '테이스트키트 얼리버드', 1011, 1204, 'PAID', 50000,
   '신규서포터2', '010-3000-0002', '서울시 강남구 테헤란로 50', '12층', '06110',
   'NONE', TIMESTAMP '2025-11-05 10:00:00', TIMESTAMP '2025-11-05 10:02:00'),
  (1406, 'ORD-20251106-GGG', '홈라이트 얼리버드', 1001, 1205, 'PAID', 220000,
   '서포터2', '010-2000-0002', '서울시 강남구 테헤란로 212', '902호', '06102',
   'NONE', TIMESTAMP '2025-11-06 11:00:00', TIMESTAMP '2025-11-06 11:05:00');

-- 주문 아이템
INSERT INTO order_items (order_id, reward_id, reward_name, reward_price, quantity, subtotal, note) VALUES
  (1400, 1301, '펄스핏 스타터 패키지', 150000, 1, 150000, '1개 구매'),
  (1401, 1301, '펄스핏 스타터 패키지', 150000, 2, 300000, '2개 구매'),
  (1402, 1303, '지오트레일 얼리버드', 180000, 1, 180000, '취소 주문'),
  (1403, 1302, '루멘노트 풀 패키지', 90000, 3, 270000, '종료된 프로젝트 주문'),
  (1404, 1303, '지오트레일 얼리버드', 180000, 1, 180000, '추가 구매'),
  (1405, 1304, '테이스트키트 얼리버드', 50000, 1, 50000, '위험 샘플'),
  (1406, 1305, '홈라이트 얼리버드', 220000, 1, 220000, '기회 샘플');

-- 결제
INSERT INTO payments (id, order_id, payment_key, amount, method, status, created_at, approved_at) VALUES
  (1500, 1400, 'pay-key-1400', 150000, 'CARD', 'DONE', TIMESTAMP '2025-11-01 10:16:00', TIMESTAMP '2025-11-01 10:17:00'),
  (1501, 1401, 'pay-key-1401', 300000, 'CARD', 'DONE', TIMESTAMP '2025-11-02 11:31:00', TIMESTAMP '2025-11-02 11:32:00'),
  (1502, 1402, 'pay-key-1402', 180000, 'CARD', 'CANCELED', TIMESTAMP '2025-11-03 12:02:00', TIMESTAMP '2025-11-03 12:05:00'),
  (1503, 1403, 'pay-key-1403', 270000, 'CARD', 'DONE', TIMESTAMP '2025-10-05 09:16:00', TIMESTAMP '2025-10-05 09:17:00'),
  (1504, 1404, 'pay-key-1404', 180000, 'CARD', 'DONE', TIMESTAMP '2025-11-04 09:11:00', TIMESTAMP '2025-11-04 09:12:00'),
  (1505, 1405, 'pay-key-1405', 50000, 'CARD', 'DONE', TIMESTAMP '2025-11-05 10:01:00', TIMESTAMP '2025-11-05 10:02:00'),
  (1506, 1406, 'pay-key-1406', 220000, 'CARD', 'DONE', TIMESTAMP '2025-11-06 11:02:00', TIMESTAMP '2025-11-06 11:04:00');

-- 환불
INSERT INTO refunds (payment_id, amount, status, reason, created_at) VALUES
  (1502, 180000, 'COMPLETED', '사용자 취소', TIMESTAMP '2025-11-03 12:06:00');

-- 플랫폼 수수료/환불 (balance_after는 누적 예시)
INSERT INTO platform_wallet_transactions (wallet_id, type, amount, balance_after, related_project_id, created_at, description) VALUES
  (1, 'PLATFORM_FEE_IN', 15000, 15000, 1201, TIMESTAMP '2025-11-01 10:18:00', '펄스핏 주문 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 30000, 45000, 1201, TIMESTAMP '2025-11-02 11:33:00', '펄스핏 주문 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 27000, 72000, 1202, TIMESTAMP '2025-10-05 09:18:00', '루멘노트 수수료 10%'),
  (1, 'REFUND_OUT', -18000, 54000, 1203, TIMESTAMP '2025-11-03 12:07:00', '지오트레일 취소 환불'),
  (1, 'PLATFORM_FEE_IN', 18000, 72000, 1203, TIMESTAMP '2025-11-04 09:13:00', '지오트레일 추가 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 5000, 77000, 1204, TIMESTAMP '2025-11-05 10:03:00', '테이스트키트 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 22000, 99000, 1205, TIMESTAMP '2025-11-06 11:06:00', '홈라이트 수수료 10%');

-- 프로젝트 지갑 트랜잭션 (DEPOSIT/REFUND 예시)
INSERT INTO project_wallet_transactions (project_wallet_id, amount, balance_after, type, description, created_at, order_id) VALUES
  (1, 150000, 150000, 'DEPOSIT', 'ORD-20251101-AAA 입금', TIMESTAMP '2025-11-01 10:18:00', 1400),
  (1, 300000, 450000, 'DEPOSIT', 'ORD-20251102-BBB 입금', TIMESTAMP '2025-11-02 11:33:00', 1401),
  (1, -67500, 382500, 'RELEASE_PENDING', '수수료 차감(플랫폼+PG)', TIMESTAMP '2025-11-02 11:34:00', NULL),
  (1, -150000, 232500, 'RELEASE', '1차 정산 지급', TIMESTAMP '2025-11-08 10:00:00', NULL),

  (2, 270000, 270000, 'DEPOSIT', 'ORD-20251005-DDD 입금', TIMESTAMP '2025-10-05 09:18:00', 1403),

  (3, 180000, 180000, 'DEPOSIT', 'ORD-20251104-EEE 입금', TIMESTAMP '2025-11-04 09:13:00', 1404),

  (4, 50000, 50000, 'DEPOSIT', 'ORD-20251105-FFF 입금', TIMESTAMP '2025-11-05 10:03:00', 1405),

  (5, 220000, 220000, 'DEPOSIT', 'ORD-20251106-GGG 입금', TIMESTAMP '2025-11-06 11:06:00', 1406);

-- 정산 (완료/진행/대기)
INSERT INTO settlements (id, project_id, maker_id, total_order_amount, toss_fee_amount, platform_fee_amount, net_amount,
                         first_payment_amount, first_payment_status, first_payment_at,
                         final_payment_amount, final_payment_status, final_payment_at,
                         status, retry_count, created_at, updated_at) VALUES
  (1600, 1202, 1003, 270000, 13500, 27000, 229500,
   100000, 'DONE', TIMESTAMP '2025-10-10 10:00:00',
   129500, 'DONE', TIMESTAMP '2025-10-20 10:00:00',
   'COMPLETED', 0, TIMESTAMP '2025-10-05 09:20:00', TIMESTAMP '2025-10-20 10:00:00'),
  (1601, 1201, 1003, 450000, 22500, 45000, 382500,
   150000, 'DONE', TIMESTAMP '2025-11-08 10:00:00',
   232500, 'PENDING', NULL,
   'FIRST_PAID', 0, TIMESTAMP '2025-11-02 11:35:00', TIMESTAMP '2025-11-08 10:00:00'),
  (1602, 1203, 1004, 360000, 18000, 36000, 306000,
   0, 'PENDING', NULL,
   306000, 'PENDING', NULL,
   'PENDING', 0, TIMESTAMP '2025-11-04 09:15:00', TIMESTAMP '2025-11-04 09:15:00'),
  (1603, 1204, 1004, 50000, 2500, 5000, 42500,
   0, 'PENDING', NULL,
   42500, 'PENDING', NULL,
   'PENDING', 0, TIMESTAMP '2025-11-05 10:05:00', TIMESTAMP '2025-11-05 10:05:00'),
  (1604, 1205, 1003, 220000, 11000, 22000, 187000,
   0, 'PENDING', NULL,
   187000, 'PENDING', NULL,
   'PENDING', 0, TIMESTAMP '2025-11-06 11:07:00', TIMESTAMP '2025-11-06 11:07:00');

-- 메이커 지갑 트랜잭션 (정산 1/2차 샘플)
INSERT INTO wallet_transactions (wallet_id, amount, balance_after, type, description, created_at, settlement_id) VALUES
  (1, 229500, 229500, 'SETTLEMENT_FINAL', '루멘노트 최종 정산 완료', TIMESTAMP '2025-10-20 10:00:00', 1600),
  (1, 150000, 379500, 'SETTLEMENT_FIRST', '펄스핏 1차 정산 완료', TIMESTAMP '2025-11-08 10:00:00', 1601);

-- ============================================================
-- 추가 데이터: 완전한 테스트를 위한 시나리오 (10월 주문, 시간대 다양화, PENDING, 환불, 종료 프로젝트)
-- ============================================================

-- 종료된 프로젝트 2개 추가 (성공 1개, 실패 1개)
INSERT INTO projects (id, maker_id, title, summary, story_markdown, goal_amount, start_at, end_at,
                      category, lifecycle_status, review_status, result_status,
                      request_at, approved_at, rejected_at, rejected_reason,
                      cover_image_url, cover_gallery, created_at, updated_at,
                      live_start_at, live_end_at)
VALUES
  -- 1206: 성공한 프로젝트 (9월 종료, 달성률 200%)
  (1206, 1003, '에코캔들 세트',
   '친환경 왁스로 만든 향초 세트입니다.',
   '## 에코캔들' || chr(10) || '지속 가능한 원료로 만든 프리미엄 향초입니다.',
   500000, DATE '2025-09-01', DATE '2025-09-30',
   'HOME_LIVING', 'ENDED', 'APPROVED', 'SUCCESS',
   TIMESTAMP '2025-08-20 09:00:00', TIMESTAMP '2025-08-22 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/ecocandle/cover.png',
   '["https://cdn.moa.dev/projects/ecocandle/gallery-1.png"]',
   TIMESTAMP '2025-08-15 09:00:00', TIMESTAMP '2025-10-01 10:00:00',
   TIMESTAMP '2025-09-01 10:00:00', TIMESTAMP '2025-09-30 23:59:00'),

  -- 1207: 실패한 프로젝트 (10월 종료, 달성률 40%)
  (1207, 1004, '스마트 식물재배기',
   'IoT 기반 자동 식물 재배 시스템입니다.',
   '## 스마트 식물재배기' || chr(10) || '물과 빛을 자동으로 조절합니다.',
   1000000, DATE '2025-10-01', DATE '2025-10-31',
   'HOME_LIVING', 'ENDED', 'APPROVED', 'FAILED',
   TIMESTAMP '2025-09-20 09:00:00', TIMESTAMP '2025-09-22 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/smartgarden/cover.png',
   '["https://cdn.moa.dev/projects/smartgarden/gallery-1.png"]',
   TIMESTAMP '2025-09-15 09:00:00', TIMESTAMP '2025-11-01 10:00:00',
   TIMESTAMP '2025-10-01 10:00:00', TIMESTAMP '2025-10-31 23:59:00');

-- 프로젝트 지갑 추가
INSERT INTO project_wallets (id, escrow_balance, pending_release, released_total, status, updated_at, project_id) VALUES
  (6, 1000000, 0, 0, 'CLOSED', TIMESTAMP '2025-10-01 10:00:00', 1206),
  (7, 400000, 0, 0, 'CLOSED', TIMESTAMP '2025-11-01 10:00:00', 1207);

-- 프로젝트 태그 추가
INSERT INTO project_tag (project_id, tag) VALUES
  (1206, '향초'), (1206, '친환경'),
  (1207, 'IoT'), (1207, '스마트홈');

-- 리워드 추가
INSERT INTO rewards (id, project_id, name, description, price, estimated_delivery_date, is_active, stock_quantity) VALUES
  (1306, 1206, '에코캔들 기본 세트', '향초 3개 세트', 100000, DATE '2025-10-15', FALSE, 0),
  (1307, 1207, '스마트 식물재배기 얼리버드', '본체 + 씨앗 키트', 400000, DATE '2025-11-30', FALSE, 0);

-- ============================================================
-- 10월 주문 추가 (월별 비교 안정화를 위해)
-- ============================================================
INSERT INTO orders (id, order_id, order_name, user_id, project_id, status, total_amount,
                    receiver_name, receiver_phone, address_line1, address_line2, zip_code,
                    delivery_status, created_at, updated_at)
VALUES
  -- 10-10: 1206 프로젝트 (성공 프로젝트)
  (1407, 'ORD-20251010-HHH', '에코캔들 기본 세트', 1000, 1206, 'PAID', 300000,
   '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236',
   'NONE', TIMESTAMP '2025-10-10 14:30:00', TIMESTAMP '2025-10-10 14:35:00'),

  -- 10-15: 1206 프로젝트
  (1408, 'ORD-20251015-III', '에코캔들 기본 세트', 1001, 1206, 'PAID', 200000,
   '서포터2', '010-2000-0002', '서울시 강남구 테헤란로 212', '902호', '06102',
   'NONE', TIMESTAMP '2025-10-15 16:20:00', TIMESTAMP '2025-10-15 16:25:00'),

  -- 10-20: 1206 프로젝트
  (1409, 'ORD-20251020-JJJ', '에코캔들 기본 세트', 1002, 1206, 'PAID', 500000,
   '서포터3', '010-2000-0003', '서울시 강남구 도산대로 45', '302호', '06018',
   'NONE', TIMESTAMP '2025-10-20 10:45:00', TIMESTAMP '2025-10-20 10:50:00'),

  -- 10-25: 1207 프로젝트 (실패 프로젝트)
  (1410, 'ORD-20251025-KKK', '스마트 식물재배기 얼리버드', 1000, 1207, 'PAID', 400000,
   '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236',
   'NONE', TIMESTAMP '2025-10-25 11:15:00', TIMESTAMP '2025-10-25 11:20:00');

-- 주문 아이템 추가 (10월)
INSERT INTO order_items (order_id, reward_id, reward_name, reward_price, quantity, subtotal, note) VALUES
  (1407, 1306, '에코캔들 기본 세트', 100000, 3, 300000, '3개 구매'),
  (1408, 1306, '에코캔들 기본 세트', 100000, 2, 200000, '2개 구매'),
  (1409, 1306, '에코캔들 기본 세트', 100000, 5, 500000, '5개 구매'),
  (1410, 1307, '스마트 식물재배기 얼리버드', 400000, 1, 400000, '실패 프로젝트 주문');

-- 결제 추가 (10월)
INSERT INTO payments (id, order_id, payment_key, amount, method, status, created_at, approved_at) VALUES
  (1507, 1407, 'pay-key-1407', 300000, 'CARD', 'DONE', TIMESTAMP '2025-10-10 14:31:00', TIMESTAMP '2025-10-10 14:32:00'),
  (1508, 1408, 'pay-key-1408', 200000, 'CARD', 'DONE', TIMESTAMP '2025-10-15 16:21:00', TIMESTAMP '2025-10-15 16:22:00'),
  (1509, 1409, 'pay-key-1409', 500000, 'CARD', 'DONE', TIMESTAMP '2025-10-20 10:46:00', TIMESTAMP '2025-10-20 10:47:00'),
  (1510, 1410, 'pay-key-1410', 400000, 'CARD', 'DONE', TIMESTAMP '2025-10-25 11:16:00', TIMESTAMP '2025-10-25 11:17:00');

-- ============================================================
-- 11월 저녁/밤 시간대 주문 추가 (시간대별 차트 다양화)
-- ============================================================
INSERT INTO orders (id, order_id, order_name, user_id, project_id, status, total_amount,
                    receiver_name, receiver_phone, address_line1, address_line2, zip_code,
                    delivery_status, created_at, updated_at)
VALUES
  -- 11-07 14:30 (오후)
  (1411, 'ORD-20251107-LLL', '펄스핏 스타터 패키지', 1002, 1201, 'PAID', 150000,
   '서포터3', '010-2000-0003', '서울시 강남구 도산대로 45', '302호', '06018',
   'NONE', TIMESTAMP '2025-11-07 14:30:00', TIMESTAMP '2025-11-07 14:35:00'),

  -- 11-08 19:45 (저녁)
  (1412, 'ORD-20251108-MMM', '지오트레일 얼리버드', 1001, 1203, 'PAID', 360000,
   '서포터2', '010-2000-0002', '서울시 강남구 테헤란로 212', '902호', '06102',
   'NONE', TIMESTAMP '2025-11-08 19:45:00', TIMESTAMP '2025-11-08 19:50:00'),

  -- 11-09 21:20 (밤)
  (1413, 'ORD-20251109-NNN', '홈라이트 얼리버드', 1000, 1205, 'PAID', 220000,
   '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236',
   'NONE', TIMESTAMP '2025-11-09 21:20:00', TIMESTAMP '2025-11-09 21:25:00');

-- 주문 아이템 추가 (11월 저녁/밤)
INSERT INTO order_items (order_id, reward_id, reward_name, reward_price, quantity, subtotal, note) VALUES
  (1411, 1301, '펄스핏 스타터 패키지', 150000, 1, 150000, '오후 주문'),
  (1412, 1303, '지오트레일 얼리버드', 180000, 2, 360000, '저녁 주문'),
  (1413, 1305, '홈라이트 얼리버드', 220000, 1, 220000, '밤 주문');

-- 결제 추가 (11월 저녁/밤)
INSERT INTO payments (id, order_id, payment_key, amount, method, status, created_at, approved_at) VALUES
  (1511, 1411, 'pay-key-1411', 150000, 'CARD', 'DONE', TIMESTAMP '2025-11-07 14:31:00', TIMESTAMP '2025-11-07 14:32:00'),
  (1512, 1412, 'pay-key-1412', 360000, 'CARD', 'DONE', TIMESTAMP '2025-11-08 19:46:00', TIMESTAMP '2025-11-08 19:47:00'),
  (1513, 1413, 'pay-key-1413', 220000, 'CARD', 'DONE', TIMESTAMP '2025-11-09 21:21:00', TIMESTAMP '2025-11-09 21:22:00');

-- ============================================================
-- PENDING 주문 추가 (결제 실패/대기 케이스)
-- ============================================================
INSERT INTO orders (id, order_id, order_name, user_id, project_id, status, total_amount,
                    receiver_name, receiver_phone, address_line1, address_line2, zip_code,
                    delivery_status, created_at, updated_at)
VALUES
  -- 11-10: PENDING (결제 시도 중)
  (1414, 'ORD-20251110-OOO', '펄스핏 스타터 패키지', 1010, 1201, 'PENDING', 150000,
   '신규서포터1', '010-3000-0001', '서울시 강남구 언주로 100', '1층', '06000',
   'NONE', TIMESTAMP '2025-11-10 15:00:00', TIMESTAMP '2025-11-10 15:05:00'),

  -- 11-11: PENDING (PG 오류)
  (1415, 'ORD-20251111-PPP', '지오트레일 얼리버드', 1011, 1203, 'PENDING', 180000,
   '신규서포터2', '010-3000-0002', '서울시 강남구 테헤란로 50', '12층', '06110',
   'NONE', TIMESTAMP '2025-11-11 16:30:00', TIMESTAMP '2025-11-11 16:35:00');

-- 주문 아이템 추가 (PENDING)
INSERT INTO order_items (order_id, reward_id, reward_name, reward_price, quantity, subtotal, note) VALUES
  (1414, 1301, '펄스핏 스타터 패키지', 150000, 1, 150000, 'PENDING 주문'),
  (1415, 1303, '지오트레일 얼리버드', 180000, 1, 180000, 'PENDING 주문');

-- 결제 추가 (PENDING - 승인되지 않음)
INSERT INTO payments (id, order_id, payment_key, amount, method, status, created_at, approved_at) VALUES
  (1514, 1414, 'pay-key-1414', 150000, 'CARD', 'READY', TIMESTAMP '2025-11-10 15:01:00', NULL),
  (1515, 1415, 'pay-key-1415', 180000, 'CARD', 'CANCELED', TIMESTAMP '2025-11-11 16:31:00', NULL);

-- ============================================================
-- 환불 추가 (환불 통계 테스트용)
-- ============================================================
-- 11-12: 펄스핏 주문 환불
INSERT INTO orders (id, order_id, order_name, user_id, project_id, status, total_amount,
                    receiver_name, receiver_phone, address_line1, address_line2, zip_code,
                    delivery_status, created_at, updated_at)
VALUES
  (1416, 'ORD-20251112-QQQ', '펄스핏 스타터 패키지', 1002, 1201, 'CANCELED', 150000,
   '서포터3', '010-2000-0003', '서울시 강남구 도산대로 45', '302호', '06018',
   'NONE', TIMESTAMP '2025-11-12 10:00:00', TIMESTAMP '2025-11-12 10:30:00');

-- 주문 아이템 추가 (환불)
INSERT INTO order_items (order_id, reward_id, reward_name, reward_price, quantity, subtotal, note) VALUES
  (1416, 1301, '펄스핏 스타터 패키지', 150000, 1, 150000, '환불 주문');

-- 결제 추가 (환불)
INSERT INTO payments (id, order_id, payment_key, amount, method, status, created_at, approved_at) VALUES
  (1516, 1416, 'pay-key-1416', 150000, 'CARD', 'CANCELED', TIMESTAMP '2025-11-12 10:01:00', TIMESTAMP '2025-11-12 10:02:00');

-- 환불 추가
INSERT INTO refunds (payment_id, amount, status, reason, created_at) VALUES
  (1516, 150000, 'COMPLETED', '단순 변심', TIMESTAMP '2025-11-12 10:31:00');

-- ============================================================
-- 플랫폼 수수료 트랜잭션 추가
-- ============================================================
INSERT INTO platform_wallet_transactions (wallet_id, type, amount, balance_after, related_project_id, created_at, description) VALUES
  -- 10월
  (1, 'PLATFORM_FEE_IN', 30000, 129000, 1206, TIMESTAMP '2025-10-10 14:33:00', '에코캔들 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 20000, 149000, 1206, TIMESTAMP '2025-10-15 16:23:00', '에코캔들 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 50000, 199000, 1206, TIMESTAMP '2025-10-20 10:48:00', '에코캔들 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 40000, 239000, 1207, TIMESTAMP '2025-10-25 11:18:00', '스마트 식물재배기 수수료 10%'),
  -- 11월
  (1, 'PLATFORM_FEE_IN', 15000, 254000, 1201, TIMESTAMP '2025-11-07 14:33:00', '펄스핏 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 36000, 290000, 1203, TIMESTAMP '2025-11-08 19:48:00', '지오트레일 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 22000, 312000, 1205, TIMESTAMP '2025-11-09 21:23:00', '홈라이트 수수료 10%'),
  (1, 'REFUND_OUT', -15000, 297000, 1201, TIMESTAMP '2025-11-12 10:32:00', '펄스핏 환불');

-- 프로젝트 지갑 트랜잭션 추가
INSERT INTO project_wallet_transactions (project_wallet_id, amount, balance_after, type, description, created_at, order_id) VALUES
  -- 10월 (1206 프로젝트)
  (6, 300000, 300000, 'DEPOSIT', 'ORD-20251010-HHH 입금', TIMESTAMP '2025-10-10 14:33:00', 1407),
  (6, 200000, 500000, 'DEPOSIT', 'ORD-20251015-III 입금', TIMESTAMP '2025-10-15 16:23:00', 1408),
  (6, 500000, 1000000, 'DEPOSIT', 'ORD-20251020-JJJ 입금', TIMESTAMP '2025-10-20 10:48:00', 1409),
  -- 10월 (1207 프로젝트)
  (7, 400000, 400000, 'DEPOSIT', 'ORD-20251025-KKK 입금', TIMESTAMP '2025-10-25 11:18:00', 1410),
  -- 11월
  (1, 150000, 382500, 'DEPOSIT', 'ORD-20251107-LLL 입금', TIMESTAMP '2025-11-07 14:33:00', 1411),
  (3, 360000, 540000, 'DEPOSIT', 'ORD-20251108-MMM 입금', TIMESTAMP '2025-11-08 19:48:00', 1412),
  (5, 220000, 440000, 'DEPOSIT', 'ORD-20251109-NNN 입금', TIMESTAMP '2025-11-09 21:23:00', 1413);

-- 정산 추가 (1206: 완료, 1207: 대기)
INSERT INTO settlements (id, project_id, maker_id, total_order_amount, toss_fee_amount, platform_fee_amount, net_amount,
                         first_payment_amount, first_payment_status, first_payment_at,
                         final_payment_amount, final_payment_status, final_payment_at,
                         status, retry_count, created_at, updated_at) VALUES
  (1605, 1206, 1003, 1000000, 50000, 100000, 850000,
   400000, 'DONE', TIMESTAMP '2025-09-15 10:00:00',
   450000, 'DONE', TIMESTAMP '2025-10-01 10:00:00',
   'COMPLETED', 0, TIMESTAMP '2025-09-01 10:00:00', TIMESTAMP '2025-10-01 10:00:00'),
  (1606, 1207, 1004, 400000, 20000, 40000, 340000,
   0, 'PENDING', NULL,
   340000, 'PENDING', NULL,
   'PENDING', 0, TIMESTAMP '2025-10-25 11:20:00', TIMESTAMP '2025-10-25 11:20:00');

-- 메이커 지갑 트랜잭션 추가 (1206 정산 완료)
INSERT INTO wallet_transactions (wallet_id, amount, balance_after, type, description, created_at, settlement_id) VALUES
  (1, 850000, 1229500, 'SETTLEMENT_FINAL', '에코캔들 최종 정산 완료', TIMESTAMP '2025-10-01 10:00:00', 1605);

-- 메이커 지갑 업데이트 (total_earned, available_balance 반영)
UPDATE maker_wallets SET
  available_balance = 1229500,
  total_earned = 1649000
WHERE id = 1;

-- 플랫폼 지갑 업데이트 (최종 잔액 반영)
UPDATE platform_wallets SET
  total_balance = 297000,
  total_platform_fee = 297000,
  updated_at = TIMESTAMP '2025-11-12 10:32:00'
WHERE id = 1;

-- 시퀀스 기반 ID 테이블 (H2는 SEQUENCE 이름이 생성됨)
ALTER SEQUENCE user_id_seq RESTART WITH 2000;
ALTER SEQUENCE maker_id_seq RESTART WITH 2000;
ALTER SEQUENCE project_id_seq RESTART WITH 2000;
ALTER SEQUENCE reward_id_seq RESTART WITH 2000;
ALTER SEQUENCE reward_set_id_seq RESTART WITH 2000;
ALTER SEQUENCE option_group_id_seq RESTART WITH 2000;
ALTER SEQUENCE option_value_id_seq RESTART WITH 2000;

-- IDENTITY 컬럼 테이블 (H2는 별도 시퀀스 이름을 생성하지 않으므로 컬럼에서 리셋)
ALTER TABLE orders ALTER COLUMN id RESTART WITH 2000;
ALTER TABLE payments ALTER COLUMN id RESTART WITH 2000;
ALTER TABLE refunds ALTER COLUMN id RESTART WITH 2000;
ALTER TABLE platform_wallet_transactions ALTER COLUMN id RESTART WITH 2000;
ALTER TABLE platform_wallets ALTER COLUMN id RESTART WITH 2000;
ALTER TABLE project_wallet_transactions ALTER COLUMN id RESTART WITH 2000;
ALTER TABLE project_wallets ALTER COLUMN id RESTART WITH 2000;
ALTER TABLE maker_wallets ALTER COLUMN id RESTART WITH 2000;
ALTER TABLE settlements ALTER COLUMN id RESTART WITH 2000;

-- ============================================================
-- 추가: 발표/통계용 대용량 시나리오 (플래그십 + 전시용)
-- ============================================================

-- 신규 서포터 & 메이커 계정
INSERT INTO users (id, email, password, name, role, onboarding_status, created_at, updated_at, last_login_at, image_url, provider) VALUES
  (1020, 'lena.park@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '이가온', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-18 09:00:00', TIMESTAMP '2025-10-18 09:00:00', TIMESTAMP '2025-10-18 09:10:00',
   'https://cdn.moa.dev/avatars/lena.png', 'LOCAL'),
  (1021, 'minseo.cho@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '최민서', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-19 09:00:00', TIMESTAMP '2025-10-19 09:00:00', TIMESTAMP '2025-10-19 09:10:00',
   'https://cdn.moa.dev/avatars/minseo.png', 'LOCAL'),
  (1022, 'joon.kim@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '김준호', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-20 09:00:00', TIMESTAMP '2025-10-20 09:00:00', TIMESTAMP '2025-10-20 09:10:00',
   'https://cdn.moa.dev/avatars/joon.png', 'LOCAL'),
  (1023, 'harin.yu@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '유하린', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-21 09:00:00', TIMESTAMP '2025-10-21 09:00:00', TIMESTAMP '2025-10-21 09:10:00',
   'https://cdn.moa.dev/avatars/harin.png', 'LOCAL'),
  (1024, 'daniel.han@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '한다니엘', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-22 09:00:00', TIMESTAMP '2025-10-22 09:00:00', TIMESTAMP '2025-10-22 09:10:00',
   'https://cdn.moa.dev/avatars/daniel.png', 'LOCAL'),
  -- 메이커 오너 계정
  (1025, 'neonlight.owner@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '네온라이트 오너', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-18 09:30:00', TIMESTAMP '2025-10-18 09:30:00', TIMESTAMP '2025-10-18 09:40:00',
   'https://cdn.moa.dev/avatars/neon.png', 'LOCAL'),
  (1026, 'stone.owner@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '스톤앤라운드 오너', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-18 09:35:00', TIMESTAMP '2025-10-18 09:35:00', TIMESTAMP '2025-10-18 09:45:00',
   'https://cdn.moa.dev/avatars/stone.png', 'LOCAL'),
  (1027, 'salt.owner@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '소금골목 오너', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-18 09:40:00', TIMESTAMP '2025-10-18 09:40:00', TIMESTAMP '2025-10-18 09:50:00',
   'https://cdn.moa.dev/avatars/salt.png', 'LOCAL'),
  (1028, 'trail.owner@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '노던트레일 오너', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-18 09:45:00', TIMESTAMP '2025-10-18 09:45:00', TIMESTAMP '2025-10-18 09:55:00',
   'https://cdn.moa.dev/avatars/trail.png', 'LOCAL'),
  (1029, 'midnight.owner@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '미드나잇 오너', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-18 09:50:00', TIMESTAMP '2025-10-18 09:50:00', TIMESTAMP '2025-10-18 10:00:00',
   'https://cdn.moa.dev/avatars/midnight.png', 'LOCAL'),
  (1030, 'brick.owner@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '브릭앤우드 오너', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-18 09:55:00', TIMESTAMP '2025-10-18 09:55:00', TIMESTAMP '2025-10-18 10:05:00',
   'https://cdn.moa.dev/avatars/brick.png', 'LOCAL'),
  (1031, 'levelup.owner@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '레벨업 오너', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-18 10:00:00', TIMESTAMP '2025-10-18 10:00:00', TIMESTAMP '2025-10-18 10:10:00',
   'https://cdn.moa.dev/avatars/levelup.png', 'LOCAL'),
  (1032, 'paper.owner@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '페이퍼웨이브 오너', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-18 10:05:00', TIMESTAMP '2025-10-18 10:05:00', TIMESTAMP '2025-10-18 10:15:00',
   'https://cdn.moa.dev/avatars/paper.png', 'LOCAL'),
  (1033, 'moonpocket.owner@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '문포켓 오너', 'USER', 'NOT_STARTED',
   TIMESTAMP '2025-10-18 10:10:00', TIMESTAMP '2025-10-18 10:10:00', TIMESTAMP '2025-10-18 10:20:00',
   'https://cdn.moa.dev/avatars/moon.png', 'LOCAL');

INSERT INTO supporter_profiles (user_id, display_name, bio, image_url, phone, postal_code, created_at, updated_at, address1, address2, interests) VALUES
  (1020, '온도 조절러', '조명/라이프스타일 제품 위주로 서포트합니다.', 'https://cdn.moa.dev/avatars/lena.png',
   '010-5000-0001', '06001', TIMESTAMP '2025-10-18 09:05:00', TIMESTAMP '2025-10-18 09:10:00',
   '서울시 서초구 반포대로 100', '701호', '["HOME_LIVING","DESIGN"]'),
  (1021, '밤의 러너', '웨어러블과 홈짐 제품을 좋아해요.', 'https://cdn.moa.dev/avatars/minseo.png',
   '010-5000-0002', '06002', TIMESTAMP '2025-10-19 09:05:00', TIMESTAMP '2025-10-19 09:10:00',
   '서울시 강남구 논현로 200', '803호', '["TECH","FASHION"]'),
  (1022, '디테일 헌터', '디자인 소품과 보드게임을 모읍니다.', 'https://cdn.moa.dev/avatars/joon.png',
   '010-5000-0003', '06003', TIMESTAMP '2025-10-20 09:05:00', TIMESTAMP '2025-10-20 09:10:00',
   '서울시 강남구 선릉로 50', '304호', '["DESIGN","GAME"]'),
  (1023, '워치 메이커', '모듈형 기기와 스마트 홈 디바이스에 관심.', 'https://cdn.moa.dev/avatars/harin.png',
   '010-5000-0004', '06004', TIMESTAMP '2025-10-21 09:05:00', TIMESTAMP '2025-10-21 09:10:00',
   '서울시 송파구 올림픽로 10', '1502호', '["TECH","HOME_LIVING"]'),
  (1024, '산책하는 큐레이터', '아트/퍼블리싱 프로젝트 서포트', 'https://cdn.moa.dev/avatars/daniel.png',
   '010-5000-0005', '06005', TIMESTAMP '2025-10-22 09:05:00', TIMESTAMP '2025-10-22 09:10:00',
   '서울시 마포구 연남로 30', '2층', '["ART","PUBLISH"]');

-- 신규 메이커
INSERT INTO makers (id, owner_user_id, maker_type, name, business_name, business_number, representative, established_at, industry_type, location, product_intro, core_competencies, image_url, contact_email, contact_phone, tech_stack, created_at, updated_at) VALUES
  (1006, 1025, 'BUSINESS', '네온라이트 랩스', '네온라이트', '310-11-000001', '오주하', DATE '2022-02-10',
   '네트워킹 장비', '서울시 마포구', '스마트 홈 네트워크 기기를 설계합니다.',
   '메쉬 네트워크, 펌웨어, UX리서치',
   'https://cdn.moa.dev/makers/neon.png', 'neonlight.owner@test.com', '010-5000-1001',
   '["Mesh","Embedded","TypeScript"]',
   TIMESTAMP '2025-10-18 10:30:00', TIMESTAMP '2025-10-18 10:30:00'),
  (1007, 1026, 'BUSINESS', '스톤앤라운드 디자인', '스톤앤라운드', '310-11-000002', '심이든', DATE '2021-06-10',
   '제품 디자인', '서울시 종로구', '데스크 액세서리와 플랜터를 만듭니다.',
   '산업 디자인, 패키징, 소재 연구',
   'https://cdn.moa.dev/makers/stone.png', 'stone.owner@test.com', '010-5000-1002',
   '["CAD","CNC","Packaging"]',
   TIMESTAMP '2025-10-18 10:35:00', TIMESTAMP '2025-10-18 10:35:00'),
  (1008, 1027, 'BUSINESS', '소금골목 다이닝', '소금골목', '310-11-000003', '박연주', DATE '2020-12-01',
   '푸드/키트', '서울시 용산구', '프리미엄 스테이크 키트를 큐레이션합니다.',
   '레시피 개발, 콜드체인, 푸드 포토',
   'https://cdn.moa.dev/makers/salt.png', 'salt.owner@test.com', '010-5000-1003',
   '["Recipe","Branding"]',
   TIMESTAMP '2025-10-18 10:40:00', TIMESTAMP '2025-10-18 10:40:00'),
  (1009, 1028, 'BUSINESS', '노던트레일 웨어', '노던트레일', '310-11-000004', '하태린', DATE '2021-04-04',
   '아웃도어 웨어', '서울시 성동구', '러닝/하이킹 라인을 만듭니다.',
   '패턴, 내구성 소재, 경량화',
   'https://cdn.moa.dev/makers/trail.png', 'trail.owner@test.com', '010-5000-1004',
   '["Pattern","Nylon","SupplyChain"]',
   TIMESTAMP '2025-10-18 10:45:00', TIMESTAMP '2025-10-18 10:45:00'),
  (1010, 1029, 'BUSINESS', '미드나잇 블루밍', '미드나잇', '310-11-000005', '이예나', DATE '2022-03-03',
   '뷰티', '서울시 강서구', '야간 루틴용 스킨케어를 연구합니다.',
   '원료 소싱, 향 블렌딩, 피부 임상',
   'https://cdn.moa.dev/makers/midnight.png', 'midnight.owner@test.com', '010-5000-1005',
   '["Formulation","Brand"]',
   TIMESTAMP '2025-10-18 10:50:00', TIMESTAMP '2025-10-18 10:50:00'),
  (1011, 1030, 'BUSINESS', '브릭앤우드 리빙', '브릭앤우드', '310-11-000006', '정윤재', DATE '2021-09-09',
   '홈리빙', '경기도 성남시', '조명/디퓨저/수납 제품을 만듭니다.',
   '목가구, 금속가공, IoT 조명',
   'https://cdn.moa.dev/makers/brick.png', 'brick.owner@test.com', '010-5000-1006',
   '["Wood","Metal","IoT"]',
   TIMESTAMP '2025-10-18 10:55:00', TIMESTAMP '2025-10-18 10:55:00'),
  (1012, 1031, 'BUSINESS', '레벨업 크리에이티브', '레벨업', '310-11-000007', '류도현', DATE '2020-08-08',
   '게임/보드', '서울시 서대문구', '보드게임과 카드 수집 프로젝트를 만듭니다.',
   '게임 기획, 인쇄, 커뮤니티',
   'https://cdn.moa.dev/makers/levelup.png', 'levelup.owner@test.com', '010-5000-1007',
   '["GameDesign","Printing"]',
   TIMESTAMP '2025-10-18 11:00:00', TIMESTAMP '2025-10-18 11:00:00'),
  (1013, 1032, 'BUSINESS', '페이퍼웨이브 스튜디오', '페이퍼웨이브', '310-11-000008', '도하림', DATE '2021-07-07',
   '아트', '서울시 중구', '프린트/실크스크린 아트워크를 만듭니다.',
   '판화, 일러스트, 컬러 매칭',
   'https://cdn.moa.dev/makers/paper.png', 'paper.owner@test.com', '010-5000-1008',
   '["Silkscreen","Illustration"]',
   TIMESTAMP '2025-10-18 11:05:00', TIMESTAMP '2025-10-18 11:05:00'),
  (1014, 1033, 'BUSINESS', '문포켓 프레스', '문포켓', '310-11-000009', '유지안', DATE '2020-11-11',
   '퍼블리싱', '서울시 마포구', '포토에세이/트래블북을 만듭니다.',
   '편집, 사진, 제본',
   'https://cdn.moa.dev/makers/moon.png', 'moonpocket.owner@test.com', '010-5000-1009',
   '["Editing","Printing"]',
   TIMESTAMP '2025-10-18 11:10:00', TIMESTAMP '2025-10-18 11:10:00');

-- 플래그십 프로젝트 (maker1=1003) 6개
INSERT INTO projects (id, maker_id, title, summary, story_markdown, goal_amount, start_at, end_at,
                      category, lifecycle_status, review_status, result_status,
                      request_at, approved_at, rejected_at, rejected_reason,
                      cover_image_url, cover_gallery, created_at, updated_at,
                      live_start_at, live_end_at)
VALUES
  (1210, 1003, '루멘플로우 스마트 램프',
   '색온도와 밝기를 자동 조절하는 데스크 램프', '## 루멘플로우' || chr(10) || '스마트 홈 연동 조명입니다.',
   35000000, DATE '2025-10-20', DATE '2025-12-15',
   'TECH', 'LIVE', 'APPROVED', 'NONE',
   TIMESTAMP '2025-10-10 09:00:00', TIMESTAMP '2025-10-12 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/lumenflow/cover.png',
   '["https://cdn.moa.dev/projects/lumenflow/gallery-1.png","https://cdn.moa.dev/projects/lumenflow/gallery-2.png"]',
   TIMESTAMP '2025-10-09 09:00:00', TIMESTAMP '2025-10-20 09:00:00',
   TIMESTAMP '2025-10-20 09:00:00', TIMESTAMP '2025-12-15 23:59:00'),
  (1211, 1003, '코어핏 모듈 밴드 v2',
   '센서 모듈을 교체하는 모듈형 밴드', '## 코어핏 모듈 밴드 v2' || chr(10) || '건강 데이터를 세분화합니다.',
   25000000, DATE '2025-10-15', DATE '2025-12-10',
   'TECH', 'LIVE', 'APPROVED', 'NONE',
   TIMESTAMP '2025-10-05 10:00:00', TIMESTAMP '2025-10-07 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/corefit/cover.png',
   '["https://cdn.moa.dev/projects/corefit/gallery-1.png","https://cdn.moa.dev/projects/corefit/gallery-2.png"]',
   TIMESTAMP '2025-10-04 09:00:00', TIMESTAMP '2025-10-15 09:00:00',
   TIMESTAMP '2025-10-15 09:00:00', TIMESTAMP '2025-12-10 23:59:00'),
  (1212, 1003, '노바트랙 미니 드론',
   '실내외 겸용 초경량 드론', '## 노바트랙' || chr(10) || '안정화 센서와 접이식 프로펠러',
   40000000, DATE '2025-10-22', DATE '2025-12-18',
   'TECH', 'LIVE', 'APPROVED', 'NONE',
   TIMESTAMP '2025-10-12 10:00:00', TIMESTAMP '2025-10-14 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/novatrack/cover.png',
   '["https://cdn.moa.dev/projects/novatrack/gallery-1.png"]',
   TIMESTAMP '2025-10-11 09:00:00', TIMESTAMP '2025-10-22 09:00:00',
   TIMESTAMP '2025-10-22 09:00:00', TIMESTAMP '2025-12-18 23:59:00'),
  (1213, 1003, '페더노트 전자페이퍼',
   '필기감에 집중한 전자페이퍼 노트', '## 페더노트' || chr(10) || '펜/필압 인식과 장시간 배터리',
   20000000, DATE '2025-09-01', DATE '2025-10-01',
   'DESIGN', 'ENDED', 'APPROVED', 'SUCCESS',
   TIMESTAMP '2025-08-10 10:00:00', TIMESTAMP '2025-08-12 11:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/feathernote/cover.png',
   '["https://cdn.moa.dev/projects/feathernote/gallery-1.png"]',
   TIMESTAMP '2025-08-05 09:00:00', TIMESTAMP '2025-10-02 09:00:00',
   TIMESTAMP '2025-09-01 09:00:00', TIMESTAMP '2025-10-01 23:59:00'),
  (1214, 1003, '애쉬그린 폴딩바이크',
   '도심형 접이식 전동 자전거', '## 애쉬그린' || chr(10) || '40km 주행, 마그네슘 프레임',
   50000000, DATE '2025-09-10', DATE '2025-10-20',
   'TECH', 'ENDED', 'APPROVED', 'NONE',
   TIMESTAMP '2025-08-15 10:00:00', TIMESTAMP '2025-08-17 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/ashgreen/cover.png',
   '["https://cdn.moa.dev/projects/ashgreen/gallery-1.png","https://cdn.moa.dev/projects/ashgreen/gallery-2.png"]',
   TIMESTAMP '2025-08-12 09:00:00', TIMESTAMP '2025-10-21 09:00:00',
   TIMESTAMP '2025-09-10 09:00:00', TIMESTAMP '2025-10-20 23:59:00'),
  (1215, 1003, '사일런트큐브 공기정화기',
   '저소음 모듈형 공기정화기', '## 사일런트큐브' || chr(10) || '필터 모듈 교체형, 저소음 설계',
   22000000, DATE '2025-12-20', DATE '2026-02-10',
   'HOME_LIVING', 'SCHEDULED', 'APPROVED', 'NONE',
   TIMESTAMP '2025-12-01 10:00:00', TIMESTAMP '2025-12-05 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/silentcube/cover.png',
   '["https://cdn.moa.dev/projects/silentcube/gallery-1.png"]',
   TIMESTAMP '2025-11-28 09:00:00', TIMESTAMP '2025-12-10 09:00:00',
   TIMESTAMP '2025-12-20 09:00:00', TIMESTAMP '2026-02-10 23:59:00');

-- 플래그십 리워드
INSERT INTO rewards (id, project_id, name, description, price, estimated_delivery_date, is_active, stock_quantity, version) VALUES
  (1310, 1210, '루멘플로우 얼리버드', '본체 + 무선 충전 스탠드', 1800000, DATE '2026-02-10', TRUE, 500, 0),
  (1311, 1211, '코어핏 모듈 밴드 키트', '밴드 + 센서 모듈 2종', 2200000, DATE '2026-01-31', TRUE, 400, 0),
  (1312, 1212, '노바트랙 미니 드론 세트', '드론 + 배터리 2팩', 2400000, DATE '2026-02-28', TRUE, 350, 0),
  (1313, 1213, '페더노트 풀 패키지', '전자페이퍼 + 펜 + 슬리브', 2000000, DATE '2025-12-15', FALSE, 0, 0),
  (1314, 1214, '애쉬그린 얼리버드', '본체 + 추가 배터리', 2500000, DATE '2026-01-20', FALSE, 0, 0),
  (1315, 1215, '사일런트큐브 예약', '본체 + 필터 2세트', 2100000, DATE '2026-04-15', TRUE, 600, 0);

-- 플래그십 주문 (루멘플로우, 하루 1건 수준)
INSERT INTO orders (id, order_id, order_name, user_id, project_id, status, total_amount,
                    receiver_name, receiver_phone, address_line1, address_line2, zip_code,
                    delivery_status, created_at, updated_at)
VALUES
  (1420, 'ORD-20251020-LF01', '루멘플로우 얼리버드', 1000, 1210, 'PAID', 1800000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-10-20 10:05:00', TIMESTAMP '2025-10-20 10:10:00'),
  (1421, 'ORD-20251021-LF02', '루멘플로우 얼리버드', 1001, 1210, 'PAID', 2200000, '서포터2', '010-2000-0002', '서울시 강남구 테헤란로 212', '902호', '06102', 'NONE', TIMESTAMP '2025-10-21 11:05:00', TIMESTAMP '2025-10-21 11:10:00'),
  (1422, 'ORD-20251022-LF03', '루멘플로우 얼리버드', 1002, 1210, 'PAID', 3500000, '서포터3', '010-2000-0003', '서울시 강남구 도산대로 45', '302호', '06018', 'NONE', TIMESTAMP '2025-10-22 12:05:00', TIMESTAMP '2025-10-22 12:10:00'),
  (1423, 'ORD-20251023-LF04', '루멘플로우 얼리버드', 1003, 1210, 'PAID', 2400000, '메이커1', '010-1111-0001', '서울시 강남구 역삼로 99', '7층', '06055', 'NONE', TIMESTAMP '2025-10-23 13:05:00', TIMESTAMP '2025-10-23 13:10:00'),
  (1424, 'ORD-20251024-LF05', '루멘플로우 얼리버드', 1004, 1210, 'PAID', 6500000, '메이커2', '010-1111-0002', '서울시 성동구 왕십리로 12', '1204호', '04799', 'NONE', TIMESTAMP '2025-10-24 14:05:00', TIMESTAMP '2025-10-24 14:10:00'),
  (1425, 'ORD-20251025-LF06', '루멘플로우 얼리버드', 1005, 1210, 'PAID', 3000000, '관리자', '010-9999-0001', '서울시 중구 을지로 15', '본사 10층', '04524', 'NONE', TIMESTAMP '2025-10-25 15:05:00', TIMESTAMP '2025-10-25 15:10:00'),
  (1426, 'ORD-20251026-LF07', '루멘플로우 얼리버드', 1010, 1210, 'PAID', 3200000, '신규서포터1', '010-3000-0001', '서울시 강남구 언주로 100', '1층', '06000', 'NONE', TIMESTAMP '2025-10-26 16:05:00', TIMESTAMP '2025-10-26 16:10:00'),
  (1427, 'ORD-20251027-LF08', '루멘플로우 얼리버드', 1011, 1210, 'PAID', 4200000, '신규서포터2', '010-3000-0002', '서울시 강남구 테헤란로 50', '12층', '06110', 'NONE', TIMESTAMP '2025-10-27 17:05:00', TIMESTAMP '2025-10-27 17:10:00'),
  (1428, 'ORD-20251028-LF09', '루멘플로우 얼리버드', 1020, 1210, 'PAID', 2700000, '이가온', '010-5000-0001', '서울시 서초구 반포대로 100', '701호', '06001', 'NONE', TIMESTAMP '2025-10-28 18:05:00', TIMESTAMP '2025-10-28 18:10:00'),
  (1429, 'ORD-20251029-LF10', '루멘플로우 얼리버드', 1021, 1210, 'PAID', 1900000, '최민서', '010-5000-0002', '서울시 강남구 논현로 200', '803호', '06002', 'NONE', TIMESTAMP '2025-10-29 19:05:00', TIMESTAMP '2025-10-29 19:10:00'),
  (1430, 'ORD-20251030-LF11', '루멘플로우 얼리버드', 1022, 1210, 'PAID', 3600000, '김준호', '010-5000-0003', '서울시 강남구 선릉로 50', '304호', '06003', 'NONE', TIMESTAMP '2025-10-30 20:05:00', TIMESTAMP '2025-10-30 20:10:00'),
  (1431, 'ORD-20251031-LF12', '루멘플로우 얼리버드', 1023, 1210, 'PAID', 2500000, '유하린', '010-5000-0004', '서울시 송파구 올림픽로 10', '1502호', '06004', 'NONE', TIMESTAMP '2025-10-31 21:05:00', TIMESTAMP '2025-10-31 21:10:00'),
  (1432, 'ORD-20251101-LF13', '루멘플로우 얼리버드', 1024, 1210, 'PAID', 3300000, '한다니엘', '010-5000-0005', '서울시 마포구 연남로 30', '2층', '06005', 'NONE', TIMESTAMP '2025-11-01 10:15:00', TIMESTAMP '2025-11-01 10:20:00'),
  (1433, 'ORD-20251102-LF14', '루멘플로우 얼리버드', 1000, 1210, 'PAID', 4800000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-02 11:15:00', TIMESTAMP '2025-11-02 11:20:00'),
  (1434, 'ORD-20251103-LF15', '루멘플로우 얼리버드', 1001, 1210, 'PAID', 5200000, '서포터2', '010-2000-0002', '서울시 강남구 테헤란로 212', '902호', '06102', 'NONE', TIMESTAMP '2025-11-03 12:15:00', TIMESTAMP '2025-11-03 12:20:00'),
  (1435, 'ORD-20251104-LF16', '루멘플로우 얼리버드', 1002, 1210, 'PENDING', 2100000, '서포터3', '010-2000-0003', '서울시 강남구 도산대로 45', '302호', '06018', 'NONE', TIMESTAMP '2025-11-04 13:15:00', TIMESTAMP '2025-11-04 13:20:00'),
  (1436, 'ORD-20251105-LF17', '루멘플로우 얼리버드', 1010, 1210, 'PENDING', 4000000, '신규서포터1', '010-3000-0001', '서울시 강남구 언주로 100', '1층', '06000', 'NONE', TIMESTAMP '2025-11-05 14:15:00', TIMESTAMP '2025-11-05 14:20:00'),
  (1437, 'ORD-20251106-LF18', '루멘플로우 얼리버드', 1011, 1210, 'PENDING', 3000000, '신규서포터2', '010-3000-0002', '서울시 강남구 테헤란로 50', '12층', '06110', 'NONE', TIMESTAMP '2025-11-06 15:15:00', TIMESTAMP '2025-11-06 15:20:00'),
  (1438, 'ORD-20251107-LF19', '루멘플로우 얼리버드', 1020, 1210, 'CANCELED', 3000000, '이가온', '010-5000-0001', '서울시 서초구 반포대로 100', '701호', '06001', 'NONE', TIMESTAMP '2025-11-07 16:15:00', TIMESTAMP '2025-11-07 16:20:00'),
  (1439, 'ORD-20251108-LF20', '루멘플로우 얼리버드', 1021, 1210, 'CANCELED', 5000000, '최민서', '010-5000-0002', '서울시 강남구 논현로 200', '803호', '06002', 'NONE', TIMESTAMP '2025-11-08 17:15:00', TIMESTAMP '2025-11-08 17:20:00');

INSERT INTO order_items (order_id, reward_id, reward_name, reward_price, quantity, subtotal, note) VALUES
  (1420, 1310, '루멘플로우 얼리버드', 1800000, 1, 1800000, '데일리 주문'),
  (1421, 1310, '루멘플로우 얼리버드', 2200000, 1, 2200000, '데일리 주문'),
  (1422, 1310, '루멘플로우 얼리버드', 3500000, 1, 3500000, '데일리 주문'),
  (1423, 1310, '루멘플로우 얼리버드', 2400000, 1, 2400000, '데일리 주문'),
  (1424, 1310, '루멘플로우 얼리버드', 6500000, 1, 6500000, '대형 주문'),
  (1425, 1310, '루멘플로우 얼리버드', 3000000, 1, 3000000, '데일리 주문'),
  (1426, 1310, '루멘플로우 얼리버드', 3200000, 1, 3200000, '데일리 주문'),
  (1427, 1310, '루멘플로우 얼리버드', 4200000, 1, 4200000, '데일리 주문'),
  (1428, 1310, '루멘플로우 얼리버드', 2700000, 1, 2700000, '데일리 주문'),
  (1429, 1310, '루멘플로우 얼리버드', 1900000, 1, 1900000, '야간 주문'),
  (1430, 1310, '루멘플로우 얼리버드', 3600000, 1, 3600000, '야간 주문'),
  (1431, 1310, '루멘플로우 얼리버드', 2500000, 1, 2500000, '야간 주문'),
  (1432, 1310, '루멘플로우 얼리버드', 3300000, 1, 3300000, '야간 주문'),
  (1433, 1310, '루멘플로우 얼리버드', 4800000, 1, 4800000, '주말 주문'),
  (1434, 1310, '루멘플로우 얼리버드', 5200000, 1, 5200000, '주말 주문'),
  (1435, 1310, '루멘플로우 얼리버드', 2100000, 1, 2100000, 'READY 주문'),
  (1436, 1310, '루멘플로우 얼리버드', 4000000, 1, 4000000, 'READY 주문'),
  (1437, 1310, '루멘플로우 얼리버드', 3000000, 1, 3000000, 'READY 주문'),
  (1438, 1310, '루멘플로우 얼리버드', 3000000, 1, 3000000, '취소 주문'),
  (1439, 1310, '루멘플로우 얼리버드', 5000000, 1, 5000000, '취소 주문');

INSERT INTO payments (id, order_id, payment_key, amount, method, status, created_at, approved_at) VALUES
  (1520, 1420, 'pay-key-1420', 1800000, 'CARD', 'DONE', TIMESTAMP '2025-10-20 10:06:00', TIMESTAMP '2025-10-20 10:07:00'),
  (1521, 1421, 'pay-key-1421', 2200000, 'CARD', 'DONE', TIMESTAMP '2025-10-21 11:06:00', TIMESTAMP '2025-10-21 11:07:00'),
  (1522, 1422, 'pay-key-1422', 3500000, 'CARD', 'DONE', TIMESTAMP '2025-10-22 12:06:00', TIMESTAMP '2025-10-22 12:07:00'),
  (1523, 1423, 'pay-key-1423', 2400000, 'CARD', 'DONE', TIMESTAMP '2025-10-23 13:06:00', TIMESTAMP '2025-10-23 13:07:00'),
  (1524, 1424, 'pay-key-1424', 6500000, 'CARD', 'DONE', TIMESTAMP '2025-10-24 14:06:00', TIMESTAMP '2025-10-24 14:07:00'),
  (1525, 1425, 'pay-key-1425', 3000000, 'CARD', 'DONE', TIMESTAMP '2025-10-25 15:06:00', TIMESTAMP '2025-10-25 15:07:00'),
  (1526, 1426, 'pay-key-1426', 3200000, 'CARD', 'DONE', TIMESTAMP '2025-10-26 16:06:00', TIMESTAMP '2025-10-26 16:07:00'),
  (1527, 1427, 'pay-key-1427', 4200000, 'CARD', 'DONE', TIMESTAMP '2025-10-27 17:06:00', TIMESTAMP '2025-10-27 17:07:00'),
  (1528, 1428, 'pay-key-1428', 2700000, 'CARD', 'DONE', TIMESTAMP '2025-10-28 18:06:00', TIMESTAMP '2025-10-28 18:07:00'),
  (1529, 1429, 'pay-key-1429', 1900000, 'CARD', 'DONE', TIMESTAMP '2025-10-29 19:06:00', TIMESTAMP '2025-10-29 19:07:00'),
  (1530, 1430, 'pay-key-1430', 3600000, 'CARD', 'DONE', TIMESTAMP '2025-10-30 20:06:00', TIMESTAMP '2025-10-30 20:07:00'),
  (1531, 1431, 'pay-key-1431', 2500000, 'CARD', 'DONE', TIMESTAMP '2025-10-31 21:06:00', TIMESTAMP '2025-10-31 21:07:00'),
  (1532, 1432, 'pay-key-1432', 3300000, 'CARD', 'DONE', TIMESTAMP '2025-11-01 10:16:00', TIMESTAMP '2025-11-01 10:17:00'),
  (1533, 1433, 'pay-key-1433', 4800000, 'CARD', 'DONE', TIMESTAMP '2025-11-02 11:16:00', TIMESTAMP '2025-11-02 11:17:00'),
  (1534, 1434, 'pay-key-1434', 5200000, 'CARD', 'DONE', TIMESTAMP '2025-11-03 12:16:00', TIMESTAMP '2025-11-03 12:17:00'),
  (1535, 1435, 'pay-key-1435', 2100000, 'CARD', 'READY', TIMESTAMP '2025-11-04 13:16:00', NULL),
  (1536, 1436, 'pay-key-1436', 4000000, 'CARD', 'READY', TIMESTAMP '2025-11-05 14:16:00', NULL),
  (1537, 1437, 'pay-key-1437', 3000000, 'CARD', 'READY', TIMESTAMP '2025-11-06 15:16:00', NULL),
  (1538, 1438, 'pay-key-1438', 3000000, 'CARD', 'CANCELED', TIMESTAMP '2025-11-07 16:16:00', TIMESTAMP '2025-11-07 16:18:00'),
  (1539, 1439, 'pay-key-1439', 5000000, 'CARD', 'CANCELED', TIMESTAMP '2025-11-08 17:16:00', TIMESTAMP '2025-11-08 17:18:00');

INSERT INTO refunds (payment_id, amount, status, reason, created_at) VALUES
  (1538, 3000000, 'COMPLETED', '사용자 취소', TIMESTAMP '2025-11-07 16:25:00');

-- 플래그십 지갑
INSERT INTO project_wallets (id, escrow_balance, pending_release, released_total, status, updated_at, project_id) VALUES
  (8, 43180000, 25908000, 17272000, 'ACTIVE', TIMESTAMP '2025-11-08 17:30:00', 1210);

-- 플래그십 지갑 트랜잭션 (DEPOSIT 합산 후 수수료 차감/부분정산)
INSERT INTO project_wallet_transactions (project_wallet_id, amount, balance_after, type, description, created_at, order_id) VALUES
  (8, 1800000, 1800000, 'DEPOSIT', 'ORD-20251020-LF01 입금', TIMESTAMP '2025-10-20 10:07:00', 1420),
  (8, 2200000, 4000000, 'DEPOSIT', 'ORD-20251021-LF02 입금', TIMESTAMP '2025-10-21 11:07:00', 1421),
  (8, 3500000, 7500000, 'DEPOSIT', 'ORD-20251022-LF03 입금', TIMESTAMP '2025-10-22 12:07:00', 1422),
  (8, 2400000, 9900000, 'DEPOSIT', 'ORD-20251023-LF04 입금', TIMESTAMP '2025-10-23 13:07:00', 1423),
  (8, 6500000, 16400000, 'DEPOSIT', 'ORD-20251024-LF05 입금', TIMESTAMP '2025-10-24 14:07:00', 1424),
  (8, 3000000, 19400000, 'DEPOSIT', 'ORD-20251025-LF06 입금', TIMESTAMP '2025-10-25 15:07:00', 1425),
  (8, 3200000, 22600000, 'DEPOSIT', 'ORD-20251026-LF07 입금', TIMESTAMP '2025-10-26 16:07:00', 1426),
  (8, 4200000, 26800000, 'DEPOSIT', 'ORD-20251027-LF08 입금', TIMESTAMP '2025-10-27 17:07:00', 1427),
  (8, 2700000, 29500000, 'DEPOSIT', 'ORD-20251028-LF09 입금', TIMESTAMP '2025-10-28 18:07:00', 1428),
  (8, 1900000, 31400000, 'DEPOSIT', 'ORD-20251029-LF10 입금', TIMESTAMP '2025-10-29 19:07:00', 1429),
  (8, 3600000, 35000000, 'DEPOSIT', 'ORD-20251030-LF11 입금', TIMESTAMP '2025-10-30 20:07:00', 1430),
  (8, 2500000, 37500000, 'DEPOSIT', 'ORD-20251031-LF12 입금', TIMESTAMP '2025-10-31 21:07:00', 1431),
  (8, 3300000, 40800000, 'DEPOSIT', 'ORD-20251101-LF13 입금', TIMESTAMP '2025-11-01 10:17:00', 1432),
  (8, 4800000, 45600000, 'DEPOSIT', 'ORD-20251102-LF14 입금', TIMESTAMP '2025-11-02 11:17:00', 1433),
  (8, 5200000, 50800000, 'DEPOSIT', 'ORD-20251103-LF15 입금', TIMESTAMP '2025-11-03 12:17:00', 1434),
  (8, -7620000, 43180000, 'RELEASE_PENDING', 'PG/플랫폼 수수료 15% 차감', TIMESTAMP '2025-11-03 12:18:00', NULL),
  (8, -17272000, 25908000, 'RELEASE', '1차 정산 지급', TIMESTAMP '2025-11-04 10:00:00', NULL);

-- 메이커/플랫폼 지갑 업데이트
INSERT INTO maker_wallets (id, maker_id, available_balance, pending_balance, total_earned, total_withdrawn, updated_at) VALUES
  (3, 1006, 0, 0, 0, 0, TIMESTAMP '2025-10-18 10:30:00'),
  (4, 1007, 0, 0, 0, 0, TIMESTAMP '2025-10-18 10:35:00'),
  (5, 1008, 0, 0, 0, 0, TIMESTAMP '2025-10-18 10:40:00'),
  (6, 1009, 0, 0, 0, 0, TIMESTAMP '2025-10-18 10:45:00'),
  (7, 1010, 0, 0, 0, 0, TIMESTAMP '2025-10-18 10:50:00'),
  (8, 1011, 0, 0, 0, 0, TIMESTAMP '2025-10-18 10:55:00'),
  (9, 1012, 0, 0, 0, 0, TIMESTAMP '2025-10-18 11:00:00'),
  (10, 1013, 0, 0, 0, 0, TIMESTAMP '2025-10-18 11:05:00'),
  (11, 1014, 0, 0, 0, 0, TIMESTAMP '2025-10-18 11:10:00');

INSERT INTO platform_wallet_transactions (wallet_id, type, amount, balance_after, related_project_id, created_at, description) VALUES
  (1, 'PLATFORM_FEE_IN', 180000, 5151000, 1210, TIMESTAMP '2025-10-20 10:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 220000, 5371000, 1210, TIMESTAMP '2025-10-21 11:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 350000, 5721000, 1210, TIMESTAMP '2025-10-22 12:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 240000, 5961000, 1210, TIMESTAMP '2025-10-23 13:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 650000, 6611000, 1210, TIMESTAMP '2025-10-24 14:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 300000, 6911000, 1210, TIMESTAMP '2025-10-25 15:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 320000, 7231000, 1210, TIMESTAMP '2025-10-26 16:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 420000, 7651000, 1210, TIMESTAMP '2025-10-27 17:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 270000, 7921000, 1210, TIMESTAMP '2025-10-28 18:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 190000, 8111000, 1210, TIMESTAMP '2025-10-29 19:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 360000, 8471000, 1210, TIMESTAMP '2025-10-30 20:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 250000, 8721000, 1210, TIMESTAMP '2025-10-31 21:07:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 330000, 9051000, 1210, TIMESTAMP '2025-11-01 10:17:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 480000, 9531000, 1210, TIMESTAMP '2025-11-02 11:17:00', '루멘플로우 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 520000, 10053000, 1210, TIMESTAMP '2025-11-03 12:17:00', '루멘플로우 수수료 10%'),
  (1, 'REFUND_OUT', -300000, 9753000, 1210, TIMESTAMP '2025-11-07 16:25:00', '루멘플로우 환불 수수료 반환');

-- 플래그십 정산
INSERT INTO settlements (id, project_id, maker_id, total_order_amount, toss_fee_amount, platform_fee_amount, net_amount,
                         first_payment_amount, first_payment_status, first_payment_at,
                         final_payment_amount, final_payment_status, final_payment_at,
                         status, retry_count, created_at, updated_at) VALUES
  (1607, 1210, 1003, 50800000, 2540000, 5080000, 43180000,
   17272000, 'DONE', TIMESTAMP '2025-11-04 10:00:00',
   25908000, 'PENDING', NULL,
   'FIRST_PAID', 0, TIMESTAMP '2025-11-03 12:20:00', TIMESTAMP '2025-11-04 10:00:00');

INSERT INTO wallet_transactions (wallet_id, amount, balance_after, type, description, created_at, settlement_id) VALUES
  (3, 17272000, 17272000, 'SETTLEMENT_FIRST', '루멘플로우 1차 정산', TIMESTAMP '2025-11-04 10:00:00', 1607);

-- 전시용 프로젝트 (maker2~10, LIVE/SCHEDULED)
INSERT INTO projects (id, maker_id, title, summary, story_markdown, goal_amount, start_at, end_at,
                      category, lifecycle_status, review_status, result_status,
                      request_at, approved_at, rejected_at, rejected_reason,
                      cover_image_url, cover_gallery, created_at, updated_at,
                      live_start_at, live_end_at)
VALUES
  (1216, 1006, '에어브릿지 메쉬 라우터', '거실/방까지 끊김 없는 메쉬 라우터', '## 에어브릿지' || chr(10) || '메쉬 네트워크 자동 최적화', 30000000, DATE '2025-11-01', DATE '2025-12-20',
   'TECH', 'LIVE', 'APPROVED', 'NONE', TIMESTAMP '2025-10-20 09:00:00', TIMESTAMP '2025-10-22 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/airbridge/cover.png',
   '["https://cdn.moa.dev/projects/airbridge/gallery-1.png"]',
   TIMESTAMP '2025-10-18 11:20:00', TIMESTAMP '2025-11-01 09:00:00',
   TIMESTAMP '2025-11-01 09:00:00', TIMESTAMP '2025-12-20 23:59:00'),
  (1217, 1006, '나노파워 배터리팩', '초경량 20000mAh 배터리팩', '## 나노파워' || chr(10) || '여행용 고속충전 배터리', 28000000, DATE '2026-01-05', DATE '2026-02-15',
   'TECH', 'SCHEDULED', 'APPROVED', 'NONE', TIMESTAMP '2025-12-01 09:00:00', TIMESTAMP '2025-12-03 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/nanopower/cover.png',
   '["https://cdn.moa.dev/projects/nanopower/gallery-1.png"]',
   TIMESTAMP '2025-11-28 09:00:00', TIMESTAMP '2025-12-05 09:00:00',
   TIMESTAMP '2026-01-05 09:00:00', TIMESTAMP '2026-02-15 23:59:00'),
  (1218, 1007, '모노브릭 데스크오거나이저', '책상 위를 정리해주는 모듈형 오거나이저', '## 모노브릭' || chr(10) || '알루미늄/우드 하이브리드', 20000000, DATE '2025-11-02', DATE '2025-12-22',
   'DESIGN', 'LIVE', 'APPROVED', 'NONE', TIMESTAMP '2025-10-21 09:00:00', TIMESTAMP '2025-10-23 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/monobrick/cover.png',
   '["https://cdn.moa.dev/projects/monobrick/gallery-1.png"]',
   TIMESTAMP '2025-10-19 09:00:00', TIMESTAMP '2025-11-02 09:00:00',
   TIMESTAMP '2025-11-02 09:00:00', TIMESTAMP '2025-12-22 23:59:00'),
  (1219, 1007, '리플폴드 플랜터', '물결 모양 폴딩 플랜터', '## 리플폴드' || chr(10) || '접어서 보관하는 실내 플랜터', 22000000, DATE '2026-01-10', DATE '2026-02-28',
   'DESIGN', 'SCHEDULED', 'APPROVED', 'NONE', TIMESTAMP '2025-12-05 09:00:00', TIMESTAMP '2025-12-07 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/ripplefold/cover.png',
   '["https://cdn.moa.dev/projects/ripplefold/gallery-1.png"]',
   TIMESTAMP '2025-11-30 09:00:00', TIMESTAMP '2025-12-10 09:00:00',
   TIMESTAMP '2026-01-10 09:00:00', TIMESTAMP '2026-02-28 23:59:00'),
  (1220, 1008, '스모크버터 스테이크 키트', '고온 버터 베이스 스테이크 키트', '## 스모크버터' || chr(10) || '건조 숙성 + 향미 버터', 35000000, DATE '2025-11-03', DATE '2025-12-23',
   'FOOD', 'LIVE', 'APPROVED', 'NONE', TIMESTAMP '2025-10-22 09:00:00', TIMESTAMP '2025-10-24 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/smokebutter/cover.png',
   '["https://cdn.moa.dev/projects/smokebutter/gallery-1.png"]',
   TIMESTAMP '2025-10-20 09:00:00', TIMESTAMP '2025-11-03 09:00:00',
   TIMESTAMP '2025-11-03 09:00:00', TIMESTAMP '2025-12-23 23:59:00'),
  (1221, 1008, '코코넛바닐라 디저트 세트', '코코넛/바닐라 디저트 4종', '## 코코넛바닐라' || chr(10) || '냉동 디저트 큐레이션', 25000000, DATE '2026-01-15', DATE '2026-02-28',
   'FOOD', 'SCHEDULED', 'APPROVED', 'NONE', TIMESTAMP '2025-12-10 09:00:00', TIMESTAMP '2025-12-12 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/coconutvanilla/cover.png',
   '["https://cdn.moa.dev/projects/coconutvanilla/gallery-1.png"]',
   TIMESTAMP '2025-12-01 09:00:00', TIMESTAMP '2025-12-15 09:00:00',
   TIMESTAMP '2026-01-15 09:00:00', TIMESTAMP '2026-02-28 23:59:00'),
  (1222, 1009, '시에라라인 소프트셸 재킷', '경량 방풍 소프트셸 재킷', '## 시에라라인' || chr(10) || '러닝/하이킹 겸용', 40000000, DATE '2025-11-04', DATE '2025-12-24',
   'FASHION', 'LIVE', 'APPROVED', 'NONE', TIMESTAMP '2025-10-23 09:00:00', TIMESTAMP '2025-10-25 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/sierraline/cover.png',
   '["https://cdn.moa.dev/projects/sierraline/gallery-1.png"]',
   TIMESTAMP '2025-10-21 09:00:00', TIMESTAMP '2025-11-04 09:00:00',
   TIMESTAMP '2025-11-04 09:00:00', TIMESTAMP '2025-12-24 23:59:00'),
  (1223, 1009, '라이트패스 러닝팩', '야간 러닝용 슬링백', '## 라이트패스' || chr(10) || '리플렉티브 + 라이트 가이드', 30000000, DATE '2026-01-20', DATE '2026-03-01',
   'FASHION', 'SCHEDULED', 'APPROVED', 'NONE', TIMESTAMP '2025-12-15 09:00:00', TIMESTAMP '2025-12-17 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/lightpath/cover.png',
   '["https://cdn.moa.dev/projects/lightpath/gallery-1.png"]',
   TIMESTAMP '2025-12-05 09:00:00', TIMESTAMP '2025-12-20 09:00:00',
   TIMESTAMP '2026-01-20 09:00:00', TIMESTAMP '2026-03-01 23:59:00'),
  (1224, 1010, '미드나잇 세럼 듀오', '야간 루틴 집중 세럼', '## 미드나잇 듀오' || chr(10) || '레티놀 + 세라마이드', 25000000, DATE '2025-11-05', DATE '2025-12-25',
   'BEAUTY', 'LIVE', 'APPROVED', 'NONE', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-26 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/midnightserum/cover.png',
   '["https://cdn.moa.dev/projects/midnightserum/gallery-1.png"]',
   TIMESTAMP '2025-10-22 09:00:00', TIMESTAMP '2025-11-05 09:00:00',
   TIMESTAMP '2025-11-05 09:00:00', TIMESTAMP '2025-12-25 23:59:00'),
  (1225, 1010, '코지바디 아로마미스트', '데일리 아로마 미스트', '## 코지바디' || chr(10) || '피부/패브릭 겸용', 20000000, DATE '2026-01-25', DATE '2026-03-05',
   'BEAUTY', 'SCHEDULED', 'APPROVED', 'NONE', TIMESTAMP '2025-12-20 09:00:00', TIMESTAMP '2025-12-22 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/cozybody/cover.png',
   '["https://cdn.moa.dev/projects/cozybody/gallery-1.png"]',
   TIMESTAMP '2025-12-10 09:00:00', TIMESTAMP '2025-12-25 09:00:00',
   TIMESTAMP '2026-01-25 09:00:00', TIMESTAMP '2026-03-05 23:59:00'),
  (1226, 1011, '엘름우드 무선 스탠드', '무선 충전 기능이 있는 조명 스탠드', '## 엘름우드' || chr(10) || '충전패드 일체형', 30000000, DATE '2025-11-06', DATE '2025-12-26',
   'HOME_LIVING', 'LIVE', 'APPROVED', 'NONE', TIMESTAMP '2025-10-25 09:00:00', TIMESTAMP '2025-10-27 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/elmwood/cover.png',
   '["https://cdn.moa.dev/projects/elmwood/gallery-1.png"]',
   TIMESTAMP '2025-10-23 09:00:00', TIMESTAMP '2025-11-06 09:00:00',
   TIMESTAMP '2025-11-06 09:00:00', TIMESTAMP '2025-12-26 23:59:00'),
  (1227, 1011, '웨이브폼 디퓨저', '곡선 디자인 무드 디퓨저', '## 웨이브폼' || chr(10) || '조명+디퓨저 2in1', 22000000, DATE '2026-02-01', DATE '2026-03-20',
   'HOME_LIVING', 'SCHEDULED', 'APPROVED', 'NONE', TIMESTAMP '2025-12-25 09:00:00', TIMESTAMP '2025-12-27 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/waveform/cover.png',
   '["https://cdn.moa.dev/projects/waveform/gallery-1.png"]',
   TIMESTAMP '2025-12-15 09:00:00', TIMESTAMP '2025-12-30 09:00:00',
   TIMESTAMP '2026-02-01 09:00:00', TIMESTAMP '2026-03-20 23:59:00'),
  (1228, 1012, '아크폴리 전술 보드게임', '세트컬렉션 전략 보드게임', '## 아크폴리' || chr(10) || '확장팩 포함 얼리버드', 50000000, DATE '2025-11-07', DATE '2025-12-27',
   'GAME', 'LIVE', 'APPROVED', 'NONE', TIMESTAMP '2025-10-26 09:00:00', TIMESTAMP '2025-10-28 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/arcpoly/cover.png',
   '["https://cdn.moa.dev/projects/arcpoly/gallery-1.png"]',
   TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-11-07 09:00:00',
   TIMESTAMP '2025-11-07 09:00:00', TIMESTAMP '2025-12-27 23:59:00'),
  (1229, 1012, '픽셀노바 카드 컬렉션', '픽셀 아트 카드 수집 시리즈', '## 픽셀노바' || chr(10) || '리미티드 프린트 런', 26000000, DATE '2026-02-10', DATE '2026-03-25',
   'GAME', 'SCHEDULED', 'APPROVED', 'NONE', TIMESTAMP '2026-01-05 09:00:00', TIMESTAMP '2026-01-07 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/pixelnova/cover.png',
   '["https://cdn.moa.dev/projects/pixelnova/gallery-1.png"]',
   TIMESTAMP '2025-12-20 09:00:00', TIMESTAMP '2026-01-10 09:00:00',
   TIMESTAMP '2026-02-10 09:00:00', TIMESTAMP '2026-03-25 23:59:00'),
  (1230, 1013, '스펙트럼 실크스크린 프린트', '한정판 실크스크린 포스터', '## 스펙트럼' || chr(10) || '2종 세트 에디션', 22000000, DATE '2025-11-08', DATE '2025-12-28',
   'ART', 'LIVE', 'APPROVED', 'NONE', TIMESTAMP '2025-10-27 09:00:00', TIMESTAMP '2025-10-29 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/spectrum/cover.png',
   '["https://cdn.moa.dev/projects/spectrum/gallery-1.png"]',
   TIMESTAMP '2025-10-25 09:00:00', TIMESTAMP '2025-11-08 09:00:00',
   TIMESTAMP '2025-11-08 09:00:00', TIMESTAMP '2025-12-28 23:59:00'),
  (1231, 1013, '드리프트 컬러링북', '여행 테마 컬러링북', '## 드리프트' || chr(10) || '아트 프린트 포함', 18000000, DATE '2026-02-15', DATE '2026-03-30',
   'ART', 'SCHEDULED', 'APPROVED', 'NONE', TIMESTAMP '2026-01-10 09:00:00', TIMESTAMP '2026-01-12 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/drift/cover.png',
   '["https://cdn.moa.dev/projects/drift/gallery-1.png"]',
   TIMESTAMP '2025-12-22 09:00:00', TIMESTAMP '2026-01-12 09:00:00',
   TIMESTAMP '2026-02-15 09:00:00', TIMESTAMP '2026-03-30 23:59:00'),
  (1232, 1014, '더라이트 포토에세이북', '밤의 도시를 담은 포토에세이', '## 더라이트' || chr(10) || '120p 하드커버', 28000000, DATE '2025-11-09', DATE '2025-12-29',
   'PUBLISH', 'LIVE', 'APPROVED', 'NONE', TIMESTAMP '2025-10-28 09:00:00', TIMESTAMP '2025-10-30 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/theright/cover.png',
   '["https://cdn.moa.dev/projects/theright/gallery-1.png"]',
   TIMESTAMP '2025-10-26 09:00:00', TIMESTAMP '2025-11-09 09:00:00',
   TIMESTAMP '2025-11-09 09:00:00', TIMESTAMP '2025-12-29 23:59:00'),
  (1233, 1014, '아틀라스 트래블 로그북', '여행 기록용 하드커버 로그북', '## 아틀라스' || chr(10) || '스티커/포켓 포함', 24000000, DATE '2026-02-20', DATE '2026-04-05',
   'PUBLISH', 'SCHEDULED', 'APPROVED', 'NONE', TIMESTAMP '2026-01-15 09:00:00', TIMESTAMP '2026-01-17 10:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/atlas/cover.png',
   '["https://cdn.moa.dev/projects/atlas/gallery-1.png"]',
   TIMESTAMP '2025-12-25 09:00:00', TIMESTAMP '2026-01-17 09:00:00',
   TIMESTAMP '2026-02-20 09:00:00', TIMESTAMP '2026-04-05 23:59:00');

-- 전시용 태그
INSERT INTO project_tag (project_id, tag) VALUES
  (1216, '메쉬'), (1216, 'WiFi'),
  (1217, '배터리'), (1217, '여행'),
  (1218, '데스크'), (1218, '오거나이저'),
  (1219, '플랜터'), (1219, '폴딩'),
  (1220, '스테이크'), (1220, '키트'),
  (1221, '디저트'), (1221, '냉동'),
  (1222, '러닝'), (1222, '재킷'),
  (1223, '슬링'), (1223, '라이트'),
  (1224, '세럼'), (1224, '야간'),
  (1225, '아로마'), (1225, '미스트'),
  (1226, '조명'), (1226, '무선충전'),
  (1227, '디퓨저'), (1227, '무드등'),
  (1228, '보드게임'), (1228, '전략'),
  (1229, '카드'), (1229, '픽셀'),
  (1230, '포스터'), (1230, '실크스크린'),
  (1231, '컬러링'), (1231, '여행'),
  (1232, '포토북'), (1232, '야경'),
  (1233, '로그북'), (1233, '여행');

-- 전시용 리워드
INSERT INTO rewards (id, project_id, name, description, price, estimated_delivery_date, is_active, stock_quantity, version) VALUES
  (1316, 1216, '에어브릿지 듀얼팩', '라우터 2팩 세트', 21000000, DATE '2026-02-10', TRUE, 200, 0),
  (1317, 1217, '나노파워 예약', '초경량 배터리팩 1개', 19000000, DATE '2026-04-10', TRUE, 300, 0),
  (1318, 1218, '모노브릭 풀세트', '모듈 오거나이저 풀 패키지', 26000000, DATE '2026-02-05', TRUE, 250, 0),
  (1319, 1219, '리플폴드 예약', '폴딩 플랜터 세트', 20000000, DATE '2026-04-15', TRUE, 300, 0),
  (1320, 1220, '스모크버터 스테이크 키트', '스테이크 4인분 세트', 52500000, DATE '2026-02-01', TRUE, 150, 0),
  (1321, 1221, '코코넛바닐라 예약', '디저트 4종 세트', 18000000, DATE '2026-04-20', TRUE, 200, 0),
  (1322, 1222, '시에라라인 재킷', '소프트셸 재킷 2착', 28000000, DATE '2026-02-15', TRUE, 180, 0),
  (1323, 1223, '라이트패스 예약', '러닝 슬링팩 1개', 15000000, DATE '2026-04-30', TRUE, 250, 0),
  (1324, 1224, '미드나잇 세럼 듀오', '세럼 2병 세트', 27500000, DATE '2026-02-12', TRUE, 300, 0),
  (1325, 1225, '코지바디 예약', '아로마 미스트 2병', 16000000, DATE '2026-05-01', TRUE, 300, 0),
  (1326, 1226, '엘름우드 스탠드', '무선 충전 스탠드 1개', 21000000, DATE '2026-02-18', TRUE, 220, 0),
  (1327, 1227, '웨이브폼 예약', '디퓨저 기본 세트', 15000000, DATE '2026-05-10', TRUE, 250, 0),
  (1328, 1228, '아크폴리 얼리버드', '본판+확장 세트', 55000000, DATE '2026-02-25', TRUE, 180, 0),
  (1329, 1229, '픽셀노바 예약', '카드 컬렉션 1세트', 18000000, DATE '2026-05-20', TRUE, 250, 0),
  (1330, 1230, '스펙트럼 포스터 세트', '포스터 2종 세트', 15400000, DATE '2026-02-08', TRUE, 220, 0),
  (1331, 1231, '드리프트 예약', '컬러링북 + 프린트', 12000000, DATE '2026-05-25', TRUE, 250, 0),
  (1332, 1232, '더라이트 포토북', '포토에세이북 1권', 36400000, DATE '2026-02-20', TRUE, 200, 0),
  (1333, 1233, '아틀라스 예약', '트래블 로그북 1권', 15000000, DATE '2026-05-30', TRUE, 250, 0);

-- 전시용 주문/결제
INSERT INTO orders (id, order_id, order_name, user_id, project_id, status, total_amount,
                    receiver_name, receiver_phone, address_line1, address_line2, zip_code,
                    delivery_status, created_at, updated_at)
VALUES
  (1440, 'ORD-20251110-AB01', '에어브릿지 듀얼팩', 1000, 1216, 'PAID', 21000000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-10 09:10:00', TIMESTAMP '2025-11-10 09:15:00'),
  (1441, 'ORD-20251111-AB02', '모노브릭 풀세트', 1000, 1218, 'PAID', 26000000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-11 10:10:00', TIMESTAMP '2025-11-11 10:15:00'),
  (1442, 'ORD-20251112-AB03', '스모크버터 스테이크 키트', 1000, 1220, 'PAID', 52500000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-12 11:10:00', TIMESTAMP '2025-11-12 11:15:00'),
  (1443, 'ORD-20251113-AB04', '시에라라인 재킷', 1000, 1222, 'PAID', 28000000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-13 12:10:00', TIMESTAMP '2025-11-13 12:15:00'),
  (1444, 'ORD-20251114-AB05', '시에라라인 재킷 추가', 1000, 1222, 'PENDING', 12000000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-14 13:10:00', TIMESTAMP '2025-11-14 13:15:00'),
  (1445, 'ORD-20251115-AB06', '미드나잇 세럼 듀오', 1000, 1224, 'PAID', 27500000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-15 14:10:00', TIMESTAMP '2025-11-15 14:15:00'),
  (1446, 'ORD-20251116-AB07', '엘름우드 스탠드', 1000, 1226, 'PAID', 21000000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-16 15:10:00', TIMESTAMP '2025-11-16 15:15:00'),
  (1447, 'ORD-20251117-AB08', '엘름우드 스탠드 추가', 1000, 1226, 'PENDING', 9000000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-17 16:10:00', TIMESTAMP '2025-11-17 16:15:00'),
  (1448, 'ORD-20251118-AB09', '아크폴리 얼리버드', 1000, 1228, 'PAID', 55000000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-18 17:10:00', TIMESTAMP '2025-11-18 17:15:00'),
  (1449, 'ORD-20251119-AB10', '스펙트럼 포스터 세트', 1000, 1230, 'PAID', 15400000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-19 09:20:00', TIMESTAMP '2025-11-19 09:25:00'),
  (1450, 'ORD-20251120-AB11', '스펙트럼 포스터 세트 추가', 1000, 1230, 'PAID', 6600000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-20 10:20:00', TIMESTAMP '2025-11-20 10:25:00'),
  (1451, 'ORD-20251121-AB12', '더라이트 포토북', 1000, 1232, 'PAID', 36400000, '서포터1', '010-2000-0001', '서울시 강남구 강남대로 321', '501호', '06236', 'NONE', TIMESTAMP '2025-11-21 11:20:00', TIMESTAMP '2025-11-21 11:25:00');

INSERT INTO order_items (order_id, reward_id, reward_name, reward_price, quantity, subtotal, note) VALUES
  (1440, 1316, '에어브릿지 듀얼팩', 21000000, 1, 21000000, '고액 단일 주문'),
  (1441, 1318, '모노브릭 풀세트', 26000000, 1, 26000000, '고액 단일 주문'),
  (1442, 1320, '스모크버터 스테이크 키트', 52500000, 1, 52500000, '고액 단일 주문'),
  (1443, 1322, '시에라라인 재킷', 28000000, 1, 28000000, '고액 단일 주문'),
  (1444, 1322, '시에라라인 재킷', 12000000, 1, 12000000, 'READY 주문'),
  (1445, 1324, '미드나잇 세럼 듀오', 27500000, 1, 27500000, '고액 단일 주문'),
  (1446, 1326, '엘름우드 스탠드', 21000000, 1, 21000000, '고액 단일 주문'),
  (1447, 1326, '엘름우드 스탠드', 9000000, 1, 9000000, 'READY 주문'),
  (1448, 1328, '아크폴리 얼리버드', 55000000, 1, 55000000, '고액 단일 주문'),
  (1449, 1330, '스펙트럼 포스터 세트', 15400000, 1, 15400000, '고액 단일 주문'),
  (1450, 1330, '스펙트럼 포스터 세트', 6600000, 1, 6600000, '추가 주문'),
  (1451, 1332, '더라이트 포토북', 36400000, 1, 36400000, '고액 단일 주문');

INSERT INTO payments (id, order_id, payment_key, amount, method, status, created_at, approved_at) VALUES
  (1540, 1440, 'pay-key-1440', 21000000, 'CARD', 'DONE', TIMESTAMP '2025-11-10 09:11:00', TIMESTAMP '2025-11-10 09:12:00'),
  (1541, 1441, 'pay-key-1441', 26000000, 'CARD', 'DONE', TIMESTAMP '2025-11-11 10:11:00', TIMESTAMP '2025-11-11 10:12:00'),
  (1542, 1442, 'pay-key-1442', 52500000, 'CARD', 'DONE', TIMESTAMP '2025-11-12 11:11:00', TIMESTAMP '2025-11-12 11:12:00'),
  (1543, 1443, 'pay-key-1443', 28000000, 'CARD', 'DONE', TIMESTAMP '2025-11-13 12:11:00', TIMESTAMP '2025-11-13 12:12:00'),
  (1544, 1444, 'pay-key-1444', 12000000, 'CARD', 'READY', TIMESTAMP '2025-11-14 13:11:00', NULL),
  (1545, 1445, 'pay-key-1445', 27500000, 'CARD', 'DONE', TIMESTAMP '2025-11-15 14:11:00', TIMESTAMP '2025-11-15 14:12:00'),
  (1546, 1446, 'pay-key-1446', 21000000, 'CARD', 'DONE', TIMESTAMP '2025-11-16 15:11:00', TIMESTAMP '2025-11-16 15:12:00'),
  (1547, 1447, 'pay-key-1447', 9000000, 'CARD', 'READY', TIMESTAMP '2025-11-17 16:11:00', NULL),
  (1548, 1448, 'pay-key-1448', 55000000, 'CARD', 'DONE', TIMESTAMP '2025-11-18 17:11:00', TIMESTAMP '2025-11-18 17:12:00'),
  (1549, 1449, 'pay-key-1449', 15400000, 'CARD', 'DONE', TIMESTAMP '2025-11-19 09:21:00', TIMESTAMP '2025-11-19 09:22:00'),
  (1550, 1450, 'pay-key-1450', 6600000, 'CARD', 'DONE', TIMESTAMP '2025-11-20 10:21:00', TIMESTAMP '2025-11-20 10:22:00'),
  (1551, 1451, 'pay-key-1451', 36400000, 'CARD', 'DONE', TIMESTAMP '2025-11-21 11:21:00', TIMESTAMP '2025-11-21 11:22:00');

-- 전시용 project_wallets
INSERT INTO project_wallets (id, escrow_balance, pending_release, released_total, status, updated_at, project_id) VALUES
  (9, 17850000, 17850000, 0, 'ACTIVE', TIMESTAMP '2025-11-10 09:12:00', 1216),
  (10, 0, 0, 0, 'ACTIVE', TIMESTAMP '2025-12-05 09:00:00', 1217),
  (11, 22100000, 22100000, 0, 'ACTIVE', TIMESTAMP '2025-11-11 10:12:00', 1218),
  (12, 0, 0, 0, 'ACTIVE', TIMESTAMP '2025-12-07 10:00:00', 1219),
  (13, 44625000, 44625000, 0, 'ACTIVE', TIMESTAMP '2025-11-12 11:12:00', 1220),
  (14, 0, 0, 0, 'ACTIVE', TIMESTAMP '2025-12-12 10:00:00', 1221),
  (15, 23800000, 23800000, 0, 'ACTIVE', TIMESTAMP '2025-11-13 12:12:00', 1222),
  (16, 0, 0, 0, 'ACTIVE', TIMESTAMP '2025-12-17 10:00:00', 1223),
  (17, 23375000, 23375000, 0, 'ACTIVE', TIMESTAMP '2025-11-15 14:12:00', 1224),
  (18, 0, 0, 0, 'ACTIVE', TIMESTAMP '2025-12-22 10:00:00', 1225),
  (19, 17850000, 17850000, 0, 'ACTIVE', TIMESTAMP '2025-11-16 15:12:00', 1226),
  (20, 0, 0, 0, 'ACTIVE', TIMESTAMP '2025-12-27 10:00:00', 1227),
  (21, 46750000, 46750000, 0, 'ACTIVE', TIMESTAMP '2025-11-18 17:12:00', 1228),
  (22, 0, 0, 0, 'ACTIVE', TIMESTAMP '2026-01-07 10:00:00', 1229),
  (23, 18700000, 18700000, 0, 'ACTIVE', TIMESTAMP '2025-11-20 10:22:00', 1230),
  (24, 0, 0, 0, 'ACTIVE', TIMESTAMP '2026-01-12 10:00:00', 1231),
  (25, 30940000, 30940000, 0, 'ACTIVE', TIMESTAMP '2025-11-21 11:22:00', 1232),
  (26, 0, 0, 0, 'ACTIVE', TIMESTAMP '2026-01-17 10:00:00', 1233);

-- 전시용 project_wallet_transactions (DEPOSIT=net)
INSERT INTO project_wallet_transactions (project_wallet_id, amount, balance_after, type, description, created_at, order_id) VALUES
  (9, 17850000, 17850000, 'DEPOSIT', 'ORD-20251110-AB01 입금', TIMESTAMP '2025-11-10 09:12:00', 1440),
  (11, 22100000, 22100000, 'DEPOSIT', 'ORD-20251111-AB02 입금', TIMESTAMP '2025-11-11 10:12:00', 1441),
  (13, 44625000, 44625000, 'DEPOSIT', 'ORD-20251112-AB03 입금', TIMESTAMP '2025-11-12 11:12:00', 1442),
  (15, 23800000, 23800000, 'DEPOSIT', 'ORD-20251113-AB04 입금', TIMESTAMP '2025-11-13 12:12:00', 1443),
  (17, 23375000, 23375000, 'DEPOSIT', 'ORD-20251115-AB06 입금', TIMESTAMP '2025-11-15 14:12:00', 1445),
  (19, 17850000, 17850000, 'DEPOSIT', 'ORD-20251116-AB07 입금', TIMESTAMP '2025-11-16 15:12:00', 1446),
  (21, 46750000, 46750000, 'DEPOSIT', 'ORD-20251118-AB09 입금', TIMESTAMP '2025-11-18 17:12:00', 1448),
  (23, 13090000, 13090000, 'DEPOSIT', 'ORD-20251119-AB10 입금', TIMESTAMP '2025-11-19 09:22:00', 1449),
  (23, 5610000, 18700000, 'DEPOSIT', 'ORD-20251120-AB11 추가 입금', TIMESTAMP '2025-11-20 10:22:00', 1450),
  (25, 30940000, 30940000, 'DEPOSIT', 'ORD-20251121-AB12 입금', TIMESTAMP '2025-11-21 11:22:00', 1451);

-- 전시용 정산 (PENDING)
INSERT INTO settlements (id, project_id, maker_id, total_order_amount, toss_fee_amount, platform_fee_amount, net_amount,
                         first_payment_amount, first_payment_status, first_payment_at,
                         final_payment_amount, final_payment_status, final_payment_at,
                         status, retry_count, created_at, updated_at) VALUES
  (1608, 1216, 1006, 21000000, 1050000, 2100000, 17850000, 0, 'PENDING', NULL, 17850000, 'PENDING', NULL, 'PENDING', 0, TIMESTAMP '2025-11-10 09:16:00', TIMESTAMP '2025-11-10 09:16:00'),
  (1609, 1218, 1007, 26000000, 1300000, 2600000, 22100000, 0, 'PENDING', NULL, 22100000, 'PENDING', NULL, 'PENDING', 0, TIMESTAMP '2025-11-11 10:16:00', TIMESTAMP '2025-11-11 10:16:00'),
  (1610, 1220, 1008, 52500000, 2625000, 5250000, 44625000, 0, 'PENDING', NULL, 44625000, 'PENDING', NULL, 'PENDING', 0, TIMESTAMP '2025-11-12 11:16:00', TIMESTAMP '2025-11-12 11:16:00'),
  (1611, 1222, 1009, 28000000, 1400000, 2800000, 23800000, 0, 'PENDING', NULL, 23800000, 'PENDING', NULL, 'PENDING', 0, TIMESTAMP '2025-11-13 12:16:00', TIMESTAMP '2025-11-13 12:16:00'),
  (1612, 1224, 1010, 27500000, 1375000, 2750000, 23375000, 0, 'PENDING', NULL, 23375000, 'PENDING', NULL, 'PENDING', 0, TIMESTAMP '2025-11-15 14:16:00', TIMESTAMP '2025-11-15 14:16:00'),
  (1613, 1226, 1011, 21000000, 1050000, 2100000, 17850000, 0, 'PENDING', NULL, 17850000, 'PENDING', NULL, 'PENDING', 0, TIMESTAMP '2025-11-16 15:16:00', TIMESTAMP '2025-11-16 15:16:00'),
  (1614, 1228, 1012, 55000000, 2750000, 5500000, 46750000, 0, 'PENDING', NULL, 46750000, 'PENDING', NULL, 'PENDING', 0, TIMESTAMP '2025-11-18 17:16:00', TIMESTAMP '2025-11-18 17:16:00'),
  (1615, 1230, 1013, 22000000, 1100000, 2200000, 18700000, 0, 'PENDING', NULL, 18700000, 'PENDING', NULL, 'PENDING', 0, TIMESTAMP '2025-11-20 10:26:00', TIMESTAMP '2025-11-20 10:26:00'),
  (1616, 1232, 1014, 36400000, 1820000, 3640000, 30940000, 0, 'PENDING', NULL, 30940000, 'PENDING', NULL, 'PENDING', 0, TIMESTAMP '2025-11-21 11:26:00', TIMESTAMP '2025-11-21 11:26:00');

-- 전시용 maker_wallets 업데이트
UPDATE maker_wallets SET available_balance = 0, pending_balance = 17850000, total_earned = 17850000, updated_at = TIMESTAMP '2025-11-10 09:16:00' WHERE maker_id = 1006;
UPDATE maker_wallets SET available_balance = 0, pending_balance = 22100000, total_earned = 22100000, updated_at = TIMESTAMP '2025-11-11 10:16:00' WHERE maker_id = 1007;
UPDATE maker_wallets SET available_balance = 0, pending_balance = 44625000, total_earned = 44625000, updated_at = TIMESTAMP '2025-11-12 11:16:00' WHERE maker_id = 1008;
UPDATE maker_wallets SET available_balance = 0, pending_balance = 23800000, total_earned = 23800000, updated_at = TIMESTAMP '2025-11-13 12:16:00' WHERE maker_id = 1009;
UPDATE maker_wallets SET available_balance = 0, pending_balance = 23375000, total_earned = 23375000, updated_at = TIMESTAMP '2025-11-15 14:16:00' WHERE maker_id = 1010;
UPDATE maker_wallets SET available_balance = 0, pending_balance = 17850000, total_earned = 17850000, updated_at = TIMESTAMP '2025-11-16 15:16:00' WHERE maker_id = 1011;
UPDATE maker_wallets SET available_balance = 0, pending_balance = 46750000, total_earned = 46750000, updated_at = TIMESTAMP '2025-11-18 17:16:00' WHERE maker_id = 1012;
UPDATE maker_wallets SET available_balance = 0, pending_balance = 18700000, total_earned = 18700000, updated_at = TIMESTAMP '2025-11-20 10:26:00' WHERE maker_id = 1013;
UPDATE maker_wallets SET available_balance = 0, pending_balance = 30940000, total_earned = 30940000, updated_at = TIMESTAMP '2025-11-21 11:26:00' WHERE maker_id = 1014;
UPDATE maker_wallets SET available_balance = 17272000, pending_balance = 25908000, total_earned = 43180000, updated_at = TIMESTAMP '2025-11-04 10:00:00' WHERE maker_id = 1003;

-- 플랫폼 수수료(전시용)
INSERT INTO platform_wallet_transactions (wallet_id, type, amount, balance_after, related_project_id, created_at, description) VALUES
  (1, 'PLATFORM_FEE_IN', 2100000, 11853000, 1216, TIMESTAMP '2025-11-10 09:12:00', '에어브릿지 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 2600000, 14453000, 1218, TIMESTAMP '2025-11-11 10:12:00', '모노브릭 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 5250000, 19703000, 1220, TIMESTAMP '2025-11-12 11:12:00', '스모크버터 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 2800000, 22503000, 1222, TIMESTAMP '2025-11-13 12:12:00', '시에라라인 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 2750000, 25253000, 1224, TIMESTAMP '2025-11-15 14:12:00', '미드나잇 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 2100000, 27353000, 1226, TIMESTAMP '2025-11-16 15:12:00', '엘름우드 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 5500000, 32853000, 1228, TIMESTAMP '2025-11-18 17:12:00', '아크폴리 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 1540000, 34393000, 1230, TIMESTAMP '2025-11-19 09:22:00', '스펙트럼 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 660000, 35053000, 1230, TIMESTAMP '2025-11-20 10:22:00', '스펙트럼 추가 수수료 10%'),
  (1, 'PLATFORM_FEE_IN', 3640000, 38693000, 1232, TIMESTAMP '2025-11-21 11:22:00', '더라이트 수수료 10%');

-- 플랫폼 지갑 업데이트 (최종 잔액 갱신)
UPDATE platform_wallets SET total_balance = 38693000, total_platform_fee = 38693000, updated_at = TIMESTAMP '2025-11-21 11:22:00' WHERE id = 1;

-- 시퀀스/IDENTITY RESTART 업데이트 (최대 ID 기준)
ALTER SEQUENCE user_id_seq RESTART WITH 1100;
ALTER SEQUENCE maker_id_seq RESTART WITH 1100;
ALTER SEQUENCE project_id_seq RESTART WITH 1400;
ALTER SEQUENCE reward_id_seq RESTART WITH 1500;
ALTER SEQUENCE reward_set_id_seq RESTART WITH 1400;
ALTER SEQUENCE option_group_id_seq RESTART WITH 1400;
ALTER SEQUENCE option_value_id_seq RESTART WITH 1400;

ALTER TABLE orders ALTER COLUMN id RESTART WITH 1600;
ALTER TABLE payments ALTER COLUMN id RESTART WITH 1700;
ALTER TABLE refunds ALTER COLUMN id RESTART WITH 1600;
ALTER TABLE platform_wallet_transactions ALTER COLUMN id RESTART WITH 3000;
ALTER TABLE platform_wallets ALTER COLUMN id RESTART WITH 100;
ALTER TABLE project_wallet_transactions ALTER COLUMN id RESTART WITH 3000;
ALTER TABLE project_wallets ALTER COLUMN id RESTART WITH 100;
ALTER TABLE maker_wallets ALTER COLUMN id RESTART WITH 100;
ALTER TABLE settlements ALTER COLUMN id RESTART WITH 4000;
COMMIT;
