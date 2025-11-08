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
-- Rewards (for project_id = 1)
-- ===================================
INSERT INTO rewards (id, project_id, name, price, is_active, stock_quantity)
VALUES (1, 1, '머그컵 - 화이트', 15000, true, 100),
       (2, 1, '머그컵 - 민트', 15000, true, 100),
       (3, 1, '머그컵 - 핑크', 17000, true, 80),
       (4, 1, '머그컵 2개 세트 (화이트+민트)', 28000, true, 50),
       (5, 1, '손수건 - 도자기 패턴', 5000, true, 200);