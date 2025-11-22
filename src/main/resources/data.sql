-- 개발용 H2 시드 데이터 (통계 테스트용 확장)
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

-- 유저 (기존 + 통계용 신규)
INSERT INTO users (id, email, password, name, role, created_at, updated_at, last_login_at, image_url, provider) VALUES
  (1000, 'user1@test.com',  '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '서포터1', 'USER',
   TIMESTAMP '2024-11-10 09:00:00', TIMESTAMP '2024-11-12 10:00:00', TIMESTAMP '2024-11-15 08:10:00',
   'https://cdn.moa.dev/avatars/user1.png', 'LOCAL'),
  (1001, 'user2@test.com',  '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '서포터2', 'USER',
   TIMESTAMP '2024-11-10 09:05:00', TIMESTAMP '2024-11-12 10:10:00', TIMESTAMP '2024-11-15 08:20:00',
   'https://cdn.moa.dev/avatars/user2.png', 'LOCAL'),
  (1002, 'user3@test.com',  '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '서포터3', 'USER',
   TIMESTAMP '2024-11-10 09:10:00', TIMESTAMP '2024-11-12 10:20:00', TIMESTAMP '2024-11-15 08:30:00',
   'https://cdn.moa.dev/avatars/user3.png', 'LOCAL'),
  (1003, 'maker1@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '메이커1', 'USER',
   TIMESTAMP '2024-11-09 14:00:00', TIMESTAMP '2024-11-12 11:00:00', TIMESTAMP '2024-11-15 07:50:00',
   'https://cdn.moa.dev/avatars/maker1.png', 'LOCAL'),
  (1004, 'maker2@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '메이커2', 'USER',
   TIMESTAMP '2024-11-09 14:05:00', TIMESTAMP '2024-11-12 11:10:00', TIMESTAMP '2024-11-15 07:40:00',
   'https://cdn.moa.dev/avatars/maker2.png', 'LOCAL'),
  (1005, 'admin@test.com',  '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '관리자', 'ADMIN',
   TIMESTAMP '2024-11-08 08:30:00', TIMESTAMP '2024-11-12 09:00:00', TIMESTAMP '2024-11-15 06:30:00',
   'https://cdn.moa.dev/avatars/admin.png', 'LOCAL'),
  -- 신규 서포터 (2025-11 가입) : 신규/리텐션 지표용
  (1010, 'newuser1@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '신규서포터1', 'USER',
   TIMESTAMP '2025-11-02 09:00:00', TIMESTAMP '2025-11-02 09:00:00', TIMESTAMP '2025-11-02 09:10:00',
   'https://cdn.moa.dev/avatars/new1.png', 'LOCAL'),
  (1011, 'newuser2@test.com', '$2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC', '신규서포터2', 'USER',
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
INSERT INTO makers (id, owner_user_id, name, business_name, business_number, representative, established_at, industry_type, location, product_intro, core_competencies, image_url, contact_email, contact_phone, tech_stack, created_at, updated_at) VALUES
  (1003, 1003, '메이커원 스튜디오', '메이커원 스튜디오', '110-22-334455', '박알리스', DATE '2021-03-15',
   '스마트 하드웨어', '서울시 강남구', '일상에서 쓰는 웨어러블 로봇을 연구합니다.',
   '하이브리드 제조, 임베디드 펌웨어, 산업 디자인',
   'https://cdn.moa.dev/makers/maker1.png', 'maker1@test.com', '010-1111-0001',
   '["Spring Boot","Embedded C","PostgreSQL"]',
   TIMESTAMP '2024-11-08 11:00:00', TIMESTAMP '2024-11-12 13:45:00'),
  (1004, 1004, '트레일랩스', 'Trail Labs Co.', '220-33-778899', '최브라이언', DATE '2020-05-20',
   '아웃도어 기어', '부산시 해운대구', '여행자와 하이커를 위한 스마트 액세서리를 만듭니다.',
   '내구성 원단, 저전력 IoT, 민첩한 공급망',
   'https://cdn.moa.dev/makers/maker2.png', 'maker2@test.com', '010-1111-0002',
   '["Kotlin","LoRa","AWS IoT"]',
   TIMESTAMP '2024-11-08 11:10:00', TIMESTAMP '2024-11-12 13:50:00');

INSERT INTO maker_wallets (maker_id, available_balance, pending_balance, total_earned, total_withdrawn, updated_at) VALUES
  (1003, 0, 0, 0, 0, TIMESTAMP '2024-11-12 13:45:00'),
  (1004, 0, 0, 0, 0, TIMESTAMP '2024-11-12 13:50:00');

-- 플랫폼 지갑 싱글턴
INSERT INTO platform_wallets (id, total_balance, total_project_deposit, total_maker_payout, total_platform_fee, created_at, updated_at)
VALUES (1, 0, 0, 0, 0, TIMESTAMP '2024-11-12 09:00:00', TIMESTAMP '2024-11-12 09:00:00');

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
   0, 'PENDING', NULL,
   382500, 'PENDING', NULL,
   'PENDING', 0, TIMESTAMP '2025-11-02 11:35:00', TIMESTAMP '2025-11-02 11:35:00'),
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
