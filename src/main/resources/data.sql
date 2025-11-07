-- ===================================
-- SELECT * FROM users;
-- SELECT * FROM maker_business_profile;
-- SELECT * FROM maker_wallet;
-- SELECT * FROM project;
-- SELECT * FROM reward;
-- ===================================
INSERT INTO users (id, email, password, name, role, created_at, updated_at)
VALUES (1, 'user@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '유저', 'USER', NOW(), NOW()),
       (2, 'maker@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '메이커', 'USER', NOW(),
        NOW()),
       (3, 'admin@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '관리자', 'ADMIN', NOW(),
        NOW());

-- 2. 메이커 사업자 프로필 (user_id=2)
INSERT INTO maker_business_profile (id, user_id, bank_name, account_number, account_holder, business_number, business_name,
                             created_at, updated_at)
VALUES (1, 2, '카카오뱅크', '3333-12-1234567', '메이커', '123-45-67890', '메이커 스튜디오', NOW(), NOW());

-- 3. 메이커 지갑 (user_id=2)
INSERT INTO maker_wallet (id, user_id, available_balance, pending_balance, total_earned, total_withdrawn, updated_at)
VALUES (1, 2, 0, 0, 0, 0, NOW());

-- 4. 프로젝트 (maker_user_id=2, 현재 펀딩 중)
INSERT INTO project (maker_user_id, title, summary, story_markdown, goal_amount, category, start_at, end_at, lifecycle_status,
    review_status, rejected_reason, approved_at, rejected_at, cover_image_url, cover_gallery,
    created_at, updated_at, live_start_at, live_end_at)
VALUES
    (
        2, '수제 도자기 머그컵 만들기',
        '전문 도예가와 함께 나만의 머그컵을 만드는 워크숍입니다. 초보자도 쉽게 따라할 수 있습니다.',
        '# 수제 도자기 머그컵 만들기 스토리\n\n초보자도 쉽게 참여할 수 있는 도자기 제작 클래스입니다.',
        5000000,'TECH',
        DATEADD('DAY', -5, CURRENT_DATE),DATEADD('DAY', 25, CURRENT_DATE),
        'SCHEDULED','APPROVED',
        NULL,NOW(),NULL,
        'https://example.com/images/mug_main.jpg',
        '[""https://example.com/images/mug_1.jpg"", ""https://example.com/images/mug_2.jpg""]',
        NOW(),NOW(),DATEADD('DAY', -5, CURRENT_TIMESTAMP),DATEADD('DAY', 25, CURRENT_TIMESTAMP)
    ),
    (
        2,'수제 디저트 만들기 클래스',
        '신선한 재료를 사용해 쿠키와 마카롱을 직접 만들어보는 클래스입니다. 초보자도 쉽게 따라 할 수 있으며, 완성된 디저트는 포장해서 선물할 수도 있습니다.',
        '# 수제 디저트 클래스 스토리\n\n달콤한 향기 가득한 쿠키, 마카롱 만들기 체험.',
        3000000,'FOOD',
        DATEADD('DAY', -2, CURRENT_DATE),DATEADD('DAY', 20, CURRENT_DATE),
        'LIVE','APPROVED',
        NULL,NOW(),NULL,
        'https://example.com/images/dessert_main.jpg',
        '[""https://example.com/images/dessert_1.jpg"", ""https://example.com/images/dessert_2.jpg""]',
        NOW(),NOW(),DATEADD('DAY', -2, CURRENT_TIMESTAMP),DATEADD('DAY', 20, CURRENT_TIMESTAMP)
    );

INSERT INTO project_tag (project_id, tag)
VALUES
    (1, '핸드메이드'),
    (1, '도자기'),
    (1, '공예'),
    (2, '베이킹'),
    (2, '디저트'),
    (2, '클래스');

-- 5. 리워드 (project_id=1)
INSERT INTO reward (id, project_id, name, price, is_active, stock_quantity)
VALUES (1, 1, '머그컵 - 화이트', 15000, true, 100),
       (2, 1, '머그컵 - 민트', 15000, true, 100),
       (3, 1, '머그컵 - 핑크', 17000, true, 80),
       (4, 1, '머그컵 2개 세트 (화이트+민트)', 28000, true, 50),
       (5, 1, '손수건 - 도자기 패턴', 5000, true, 200);

-- ===================================
-- 1. 사용자 데이터 (비밀번호: test1234)
-- ⚠️ 주석 처리: 회원가입 API를 통해 생성하세요!
-- BCrypt 해시 문제로 인해 직접 INSERT 대신 API 사용 권장
--
-- 회원가입 방법:
-- POST http://localhost:8080/api/auth/signup
-- { "email": "user@test.com", "password": "test1234", "name": "유저" }
-- { "email": "maker@test.com", "password": "test1234", "name": "메이커" }
-- { "email": "admin@test.com", "password": "test1234", "name": "관리자" }
-- ===================================
-- 이메일: user@test.com, maker@test.com, admin@test.com
-- 비밀번호: test1234 (모두 동일)
-- 프로젝트: id=1 (메이커 id=2)
-- 리워드: id=1~5 (프로젝트 id=1)
-- ===================================
