-- 개발용 H2 시드 데이터입니다.
-- 자동 생성 ID는 1부터 시작하므로, 충돌을 피하기 위해 모든 PK를 1000 이상으로 지정했습니다.

SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE project_tag;
TRUNCATE TABLE reward_option_values;
TRUNCATE TABLE reward_option_groups;
TRUNCATE TABLE reward_sets;
TRUNCATE TABLE rewards;
TRUNCATE TABLE projects;
TRUNCATE TABLE makers;
TRUNCATE TABLE supporter_profiles;
TRUNCATE TABLE users;
SET REFERENTIAL_INTEGRITY TRUE;

-- 비밀번호 "test1234"의 bcrypt 해시 (공통 사용)
-- $2b$10$JTxQ0TnfmMtfGiEvKVCE3eSLPHBSNBrRO1FoH1ZmJXSBmHjN.OKYC

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
   'https://cdn.moa.dev/avatars/admin.png', 'LOCAL');

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
   '서울시 중구 을지로 15', '본사 10층', '["플랫폼","운영"]');

INSERT INTO makers (id, owner_user_id, name, business_name, business_number, representative, established_at, industry_type, location, product_intro, core_competencies, image_url, contact_email, contact_phone, tech_stack_json, created_at, updated_at) VALUES
  (1100, 1003, '메이커원 스튜디오', '메이커원 스튜디오', '110-22-334455', '박알리스', DATE '2021-03-15',
   '스마트 하드웨어', '서울시 강남구', '일상에서 쓰는 웨어러블 로봇을 연구합니다.',
   '하이브리드 제조, 임베디드 펌웨어, 산업 디자인',
   'https://cdn.moa.dev/makers/maker1.png', 'maker1@test.com', '010-1111-0001',
   '["Spring Boot","Embedded C","PostgreSQL"]',
   TIMESTAMP '2024-11-08 11:00:00', TIMESTAMP '2024-11-12 13:45:00'),
  (1101, 1004, '트레일랩스', 'Trail Labs Co.', '220-33-778899', '최브라이언', DATE '2020-05-20',
   '아웃도어 기어', '부산시 해운대구', '여행자와 하이커를 위한 스마트 액세서리를 만듭니다.',
   '내구성 원단, 저전력 IoT, 민첩한 공급망',
   'https://cdn.moa.dev/makers/maker2.png', 'maker2@test.com', '010-1111-0002',
   '["Kotlin","LoRa","AWS IoT"]',
   TIMESTAMP '2024-11-08 11:10:00', TIMESTAMP '2024-11-12 13:50:00');

INSERT INTO projects (id, maker_id, title, summary, story_markdown, goal_amount, start_at, end_at,
                      category, lifecycle_status, review_status, result_status,
                      request_at, approved_at, rejected_at, rejected_reason,
                      cover_image_url, cover_gallery, created_at, updated_at, live_start_at, live_end_at)
VALUES
  (1200, 1100, '오로라 자동조명',
   '하루 리듬에 맞춰 색온도를 조절하는 책상 조명입니다.',
   '## 오로라 자동조명' || CHAR(10) || '재택 근무자에게 건강한 빛 환경을 제공합니다.',
   2000000, DATE '2024-12-10', DATE '2025-01-20',
   'TECH', 'SCHEDULED', 'APPROVED', 'NONE',
   TIMESTAMP '2024-11-05 09:00:00', TIMESTAMP '2024-11-07 15:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/aurora/cover.png',
   '["https://cdn.moa.dev/projects/aurora/gallery-1.png","https://cdn.moa.dev/projects/aurora/gallery-2.png"]',
   TIMESTAMP '2024-11-01 09:00:00', TIMESTAMP '2024-11-12 11:00:00',
   TIMESTAMP '2024-12-10 09:00:00', TIMESTAMP '2025-01-20 23:59:00'),

  (1201, 1100, '펄스핏 모듈 밴드',
   '센서를 교체하며 데이터를 맞춤 수집하는 피트니스 밴드입니다.',
   '## 펄스핏 모듈 밴드' || CHAR(10) || '스타일을 유지하면서도 유의미한 바이오 데이터를 기록합니다.',
   3000000, DATE '2024-11-01', DATE '2024-12-15',
   'TECH', 'LIVE', 'APPROVED', 'NONE',
   TIMESTAMP '2024-10-20 10:00:00', TIMESTAMP '2024-10-22 13:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/pulsefit/cover.png',
   '["https://cdn.moa.dev/projects/pulsefit/gallery-1.png","https://cdn.moa.dev/projects/pulsefit/gallery-2.png"]',
   TIMESTAMP '2024-10-15 09:30:00', TIMESTAMP '2024-11-12 11:10:00',
   TIMESTAMP '2024-11-01 10:00:00', TIMESTAMP '2024-12-15 23:59:00'),

  (1202, 1100, '루멘노트 전자노트',
   '종이 질감을 살리고 배터리 걱정이 없는 전자 필기장입니다.',
   '## 루멘노트' || CHAR(10) || '종이 같은 필기감과 클라우드 동기화를 동시에 제공합니다.',
   1500000, DATE '2024-09-01', DATE '2024-10-01',
   'DESIGN', 'ENDED', 'APPROVED', 'SUCCESS',
   TIMESTAMP '2024-08-01 08:00:00', TIMESTAMP '2024-08-03 14:00:00', NULL, NULL,
   'https://cdn.moa.dev/projects/lumennote/cover.png',
   '["https://cdn.moa.dev/projects/lumennote/gallery-1.png","https://cdn.moa.dev/projects/lumennote/gallery-2.png"]',
   TIMESTAMP '2024-07-28 11:45:00', TIMESTAMP '2024-10-05 12:00:00',
   TIMESTAMP '2024-09-01 10:00:00', TIMESTAMP '2024-10-01 23:59:00'),

  (1203, 1101, '지오트레일 스마트 백팩',
   '태양광 패널과 LTE 트래커를 내장한 여행용 백팩입니다.',
   '## 지오트레일 스마트 백팩' || CHAR(10) || '밤길에서도 안전하게 이동하고 언제든 위치를 확인하세요.',
   2500000, DATE '2024-11-05', DATE '2024-12-31',
   'FASHION', 'LIVE', 'APPROVED', 'NONE',
   TIMESTAMP '2024-10-18 11:00:00', TIMESTAMP '2024-10-21 09:30:00', NULL, NULL,
   'https://cdn.moa.dev/projects/geotrail/cover.png',
   '["https://cdn.moa.dev/projects/geotrail/gallery-1.png","https://cdn.moa.dev/projects/geotrail/gallery-2.png"]',
   TIMESTAMP '2024-10-12 10:00:00', TIMESTAMP '2024-11-12 11:20:00',
   TIMESTAMP '2024-11-05 09:30:00', TIMESTAMP '2024-12-31 23:59:00');

INSERT INTO project_tag (project_id, tag) VALUES
  (1200, '조명'),
  (1200, '스마트홈'),
  (1201, '피트니스'),
  (1201, '웨어러블'),
  (1202, '생산성'),
  (1202, '페이퍼리스'),
  (1203, '아웃도어'),
  (1203, '여행');

INSERT INTO rewards (id, project_id, name, description, price, estimated_delivery_date, is_active, stock_quantity) VALUES
  (1300, 1200, '오로라 얼리버드 세트', '본체 + 디퓨저 + 패브릭 케이블 구성', 120000,
   DATE '2025-02-15', TRUE, 200),
  (1301, 1201, '펄스핏 스타터 패키지', '기본 밴드와 센서 카트리지 2종 포함', 150000,
   DATE '2025-01-20', TRUE, 250),
  (1302, 1202, '루멘노트 풀 패키지', '전자노트 + 스타일러스 + 폴리오 커버', 90000,
   DATE '2024-12-05', FALSE, 0),
  (1303, 1203, '지오트레일 얼리버드', '태양광 패널과 비상 비컨을 포함한 백팩', 180000,
   DATE '2025-02-01', TRUE, 180);
