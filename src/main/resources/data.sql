-- 개발용 H2 시드 데이터 (통계/정산/지갑 확인용 풀 세트)
-- 용도: /api/admin/statistics*, 정산/지갑 화면, 스케줄러(NPE 방지) 로컬 스모크 테스트
-- 계정:
--   서포터: 1000~1002 (user1~3@test.com), 신규 1010(newuser1@test.com), 1011(newuser2@test.com)
--   메이커: 1003(maker1@test.com), 1004(maker2@test.com)
--   관리자: 1005(admin@test.com / test1234)
-- 프로젝트: 1200~1205 (LIVE/ENDED/SCHEDULED 섞임, 카테고리 다양)
-- 주문/결제/환불: orders 1400~1406, payments 1500~1506, refunds 1502(취소 환불)
-- 수수료: platform_wallet_transactions 7건 (수수료 99,000, 환불 -18,000 반영)
-- 정산: settlements 5건 (1201 부분정산: 1차 150,000 지급, 잔액 232,500 대기)
-- 지갑:
--   project_wallets/transactions: 1201 부분정산 흐름 + 기타 입금
--   maker_wallets/transactions: 1003 available 379,500 / pending 419,500, 1004 pending 348,500
--   platform_wallets: balance 99,000
-- 리셋: TRUNCATE 후 insert, 시퀀스/IDENTITY RESTART 포함
-- 비밀번호 "test1234"의 bcrypt 해시: $2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC

SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE payments;
TRUNCATE TABLE refunds;
TRUNCATE TABLE platform_wallet_transactions;
TRUNCATE TABLE settlements;
TRUNCATE TABLE maker_wallets;
TRUNCATE TABLE platform_wallets;
TRUNCATE TABLE project_tag;
TRUNCATE TABLE rewards;
TRUNCATE TABLE projects;
TRUNCATE TABLE makers;
TRUNCATE TABLE supporter_profiles;
TRUNCATE TABLE users;
SET REFERENTIAL_INTEGRITY TRUE;

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
   '## 오로라 자동조명' || CHAR(10) || '재택 근무자에게 건강한 빛 환경을 제공합니다.',
   2000000, DATE '2025-11-13', DATE '2026-01-20',
   'TECH', 'SCHEDULED', 'APPROVED', 'NONE',
   TIMESTAMP '2025-11-05 09:00:00', TIMESTAMP '2025-11-07 15:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/aurora/cover.png',
   '["https://cdn.moa.dev/projects/aurora/gallery-1.png","https://cdn.moa.dev/projects/aurora/gallery-2.png"]',
   TIMESTAMP '2025-11-01 09:00:00', TIMESTAMP '2025-11-12 11:00:00',
   TIMESTAMP '2025-12-10 09:00:00', TIMESTAMP '2026-01-20 23:59:00'),

  (1201, 1003, '펄스핏 모듈 밴드',
   '센서를 교체하며 데이터를 맞춤 수집하는 피트니스 밴드입니다.',
   '## 펄스핏 모듈 밴드' || CHAR(10) || '스타일을 유지하면서도 유의미한 바이오 데이터를 기록합니다.',
   3000000, DATE '2025-11-01', DATE '2025-12-15',
   'TECH', 'LIVE', 'APPROVED', 'NONE',
   TIMESTAMP '2025-10-20 10:00:00', TIMESTAMP '2025-10-22 13:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/pulsefit/cover.png',
   '["https://cdn.moa.dev/projects/pulsefit/gallery-1.png","https://cdn.moa.dev/projects/pulsefit/gallery-2.png"]',
   TIMESTAMP '2025-10-15 09:30:00', TIMESTAMP '2025-11-12 11:10:00',
   TIMESTAMP '2025-11-01 10:00:00', TIMESTAMP '2025-12-15 23:59:00'),

  (1202, 1003, '루멘노트 전자노트',
   '종이 질감을 살리고 배터리 걱정이 없는 전자 필기장입니다.',
   '## 루멘노트' || CHAR(10) || '종이 같은 필기감과 클라우드 동기화를 동시에 제공합니다.',
   1500000, DATE '2025-09-01', DATE '2025-10-01',
   'DESIGN', 'ENDED', 'APPROVED', 'SUCCESS',
   TIMESTAMP '2025-08-01 08:00:00', TIMESTAMP '2025-08-03 14:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/lumennote/cover.png',
   '["https://cdn.moa.dev/projects/lumennote/gallery-1.png","https://cdn.moa.dev/projects/lumennote/gallery-2.png"]',
   TIMESTAMP '2025-07-28 11:45:00', TIMESTAMP '2025-10-05 12:00:00',
   TIMESTAMP '2025-09-01 10:00:00', TIMESTAMP '2025-10-01 23:59:00'),

  (1203, 1004, '지오트레일 스마트 백팩',
   '태양광 패널과 LTE 트래커를 내장한 여행용 백팩입니다.',
   '## 지오트레일 스마트 백팩' || CHAR(10) || '밤길에서도 안전하게 이동하고 언제든 위치를 확인하세요.',
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
INSERT INTO rewards (id, project_id, name, description, price, estimated_delivery_date, is_active, stock_quantity) VALUES
  (1300, 1200, '오로라 얼리버드 세트', '본체 + 디퓨저 + 패브릭 케이블 구성', 120000, DATE '2026-02-15', TRUE, 200),
  (1301, 1201, '펄스핏 스타터 패키지', '기본 밴드와 센서 카트리지 2종 포함', 150000, DATE '2026-01-20', TRUE, 250),
  (1302, 1202, '루멘노트 풀 패키지', '전자노트 + 스타일러스 + 폴리오 커버', 90000, DATE '2025-12-05', FALSE, 0),
  (1303, 1203, '지오트레일 얼리버드', '태양광 패널과 비상 비컨을 포함한 백팩', 180000, DATE '2025-12-15', TRUE, 180),
  (1304, 1204, '테이스트키트 얼리버드', '즉석 조리 키트 샘플', 50000, DATE '2025-12-30', TRUE, 100),
  (1305, 1205, '홈라이트 얼리버드', '고속충전 LED 스탠드', 220000, DATE '2026-01-10', TRUE, 150);

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
   '## 에코캔들' || CHAR(10) || '지속 가능한 원료로 만든 프리미엄 향초입니다.',
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
   '## 스마트 식물재배기' || CHAR(10) || '물과 빛을 자동으로 조절합니다.',
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
