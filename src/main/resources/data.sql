-- ===================================
-- Users
-- 1: 일반유저, 2: 메이커, 3: 관리자
-- ===================================
INSERT INTO users (id, email, password, name, role, created_at, updated_at)
VALUES (1, 'user@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '유저', 'USER', NOW(), NOW()),
       (2, 'maker@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '메이커', 'USER', NOW(),
        NOW()),
       (3, 'admin@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '관리자', 'ADMIN', NOW(),
        NOW());

-- ===================================
-- Maker specific data (for user_id = 2)
-- ===================================
INSERT INTO makers (id, owner_user_id, name, business_name, business_number, representative, established_at, industry_type,
    location, product_intro, core_competencies, image_url, contact_email, contact_phone, tech_stack_json, created_at,updated_at)
VALUES (
           2, 2,'메이커 스튜디오','메이커 스튜디오','123-45-67890','홍길동',
           DATE '2020-05-20','식품 제조업','서울특별시 마포구 독막로 12길 34',
           '수제 과일청을 만드는 건강한 브랜드입니다.', '자체 생산 및 지역 농가 협업',
           'https://example.com/maker.jpg','maker@example.com',
           '010-1234-5678','{"skills": ["Branding", "FoodTech", "Design"]}',
           NOW(), NOW());

INSERT INTO maker_wallets (id, maker_id, available_balance, pending_balance, total_earned, total_withdrawn, updated_at)
VALUES (1, 2, 0, 0, 0, 0, NOW());

-- ===================================
-- Projects (both by maker_id = 2)
-- ===================================
INSERT INTO projects (id, maker_id, title, summary, story_markdown, goal_amount, category, start_at, end_at,
                     lifecycle_status, review_status, rejected_reason, approved_at, rejected_at, cover_image_url,
                     cover_gallery, created_at, updated_at, live_start_at, live_end_at)
VALUES (1, 2, '수제 도자기 머그컵 만들기',
        '전문 도예가와 함께 나만의 머그컵을 만드는 워크숍입니다. 초보자도 쉽게 따라할 수 있습니다.',
        '# 수제 도자기 머그컵 만들기 스토리\n\n초보자도 쉽게 참여할 수 있는 도자기 제작 클래스입니다.',
        5000000, 'TECH',
        DATEADD('DAY', -5, CURRENT_DATE), DATEADD('DAY', 25, CURRENT_DATE),
        'LIVE', 'APPROVED',
        NULL, NOW(), NULL,
        'https://example.com/images/mug_main.jpg',
        '["https://example.com/images/mug_1.jpg", "https://example.com/images/mug_2.jpg"]',
        NOW(), NOW(), DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', 25, CURRENT_TIMESTAMP)),
       (2, 2, '수제 디저트 만들기 클래스',
        '신선한 재료를 사용해 쿠키와 마카롱을 직접 만들어보는 클래스입니다. 초보자도 쉽게 따라 할 수 있으며, 완성된 디저트는 포장해서 선물할 수도 있습니다.',
        '# 수제 디저트 클래스 스토리\n\n달콤한 향기 가득한 쿠키, 마카롱 만들기 체험.',
        3000000, 'FOOD',
        DATEADD('DAY', -2, CURRENT_DATE), DATEADD('DAY', 20, CURRENT_DATE),
        'SCHEDULED', 'APPROVED',
        NULL, NOW(), NULL,
        'https://example.com/images/dessert_main.jpg',
        '["https://example.com/images/dessert_1.jpg", "https://example.com/images/dessert_2.jpg"]',
        NOW(), NOW(), DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', 20, CURRENT_TIMESTAMP));

-- ===================================
-- Project Tags
-- ===================================
INSERT INTO project_tag (project_id, tag)
VALUES (1, '핸드메이드'),
       (1, '도자기'),
       (1, '공예'),
       (2, '베이킹'),
       (2, '디저트'),
       (2, '클래스');

-- ===================================
-- 🏺 Project 1 : 수제 도자기 머그컵 만들기
-- ===================================
-- 이 프로젝트는 총 5개의 리워드를 가지고 있음:
-- ① 머그컵 단일 3종 (화이트 / 민트 / 핑크)
-- ② 머그컵 2개 세트 (화이트+민트) - 옵션형 (색상, 포장)
-- ③ 손수건 - 도자기 패턴 (단일형)
-- ➕ 추가로 ‘머그컵 세트 스페셜 에디션’ 세트 구성 리워드 포함

INSERT INTO rewards (id, project_id, name, description, price, is_active, stock_quantity)
VALUES
    (1, 1, '머그컵 - 화이트', '깔끔한 화이트 컬러의 도자기 머그컵입니다.', 15000, true, 100),
    (2, 1, '머그컵 - 민트', '산뜻한 민트 컬러로 제작된 머그컵입니다.', 15000, true, 100),
    (3, 1, '머그컵 - 핑크', '부드러운 핑크톤의 머그컵입니다.', 17000, true, 80),
    (4, 1, '머그컵 2개 세트 (화이트+민트)', '화이트와 민트 머그컵이 세트로 구성되어 있습니다.', 28000, true, 50),
    (5, 1, '손수건 - 도자기 패턴', '머그컵 패턴이 새겨진 손수건입니다.', 5000, true, 200);

-- ===================================
-- 🧩 Reward 4 : 머그컵 2개 세트 (화이트+민트)
-- ===================================
-- 이 리워드는 2개의 옵션 그룹을 가지고 있음:
-- ① 머그컵 색상 선택 (화이트 / 민트 / 핑크)
-- ② 포장 방식 (기본 / 선물용)

INSERT INTO reward_option_groups (id, reward_id, group_name)
VALUES
    (1, 4, '머그컵 색상 선택'),
    (2, 4, '포장 방식');

INSERT INTO reward_option_values (id, reward_option_group_id, option_value, add_price, stock_quantity)
VALUES
    -- 머그컵 색상 선택
    (1, 1, '화이트', 0, 25),
    (2, 1, '민트', 0, 25),
    (3, 1, '핑크', 2000, 25),

    -- 포장 방식
    (4, 2, '기본 포장', 0, 30),
    (5, 2, '선물용 포장', 3000, 20);

-- ===================================
-- 🎁 RewardSet 1 : 머그컵 세트 스페셜 에디션
-- ===================================
-- Reward 4(머그컵 세트)의 하위 세트 구성 리워드
-- 세트 내부에는 두 개의 그룹 존재:
-- ① 세트 구성 선택 (화이트 / 민트 / 핑크)
-- ② 추가 구성품 선택 (손수건 / 포스터 / 선물 상자)

INSERT INTO reward_sets (id, reward_id, set_name, stock_quantity)
VALUES
    (1, 4, '머그컵 세트 스페셜 에디션', 20);

INSERT INTO reward_option_groups (id, reward_set_id, group_name)
VALUES
    (3, 1, '세트 구성 선택'),
    (4, 1, '추가 구성품 선택');

INSERT INTO reward_option_values (id, reward_option_group_id, option_value, add_price, stock_quantity)
VALUES
    -- 세트 구성 선택
    (6, 3, '화이트 머그컵', 0, 10),
    (7, 3, '민트 머그컵', 0, 10),

    -- 추가 구성품 선택
    (8, 4, '손수건', 0, 10),
    (9, 4, '포스터', 3000, 10)
