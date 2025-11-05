-- ===================================
-- SELECT * FROM users;
-- SELECT * FROM creator_profile;
-- SELECT * FROM creator_wallet;
-- SELECT * FROM project;
-- SELECT * FROM reward;
-- ===================================
INSERT INTO users (id, email, password, name, role, creator_status, created_at, updated_at)
VALUES (1, 'backer@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '후원자', 'BACKER', 'NONE',
        NOW(), NOW()),
       (2, 'creator@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '크리에이터', 'CREATOR',
        'APPROVED', NOW(), NOW()),
       (3, 'admin@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '관리자', 'ADMIN', 'NONE',
        NOW(), NOW());

-- 2. 크리에이터 프로필 (user_id=2)
INSERT INTO creator_profile (id, user_id, bank_name, account_number, account_holder, business_number, business_name,
                             created_at, updated_at)
VALUES (1, 2, '카카오뱅크', '3333-12-1234567', '크리에이터', '123-45-67890', '크리에이터 스튜디오', NOW(), NOW());

-- 3. 크리에이터 지갑 (user_id=2)
INSERT INTO creator_wallet (id, user_id, available_balance, pending_balance, total_earned, total_withdrawn, updated_at)
VALUES (1, 2, 0, 0, 0, 0, NOW());

-- 4. 프로젝트 (creator_user_id=2, 현재 펀딩 중)
INSERT INTO project (id, creator_user_id, title, content, goal_amount, category, start_at, end_at, status, created_at, updated_at)
VALUES
    (1, 2,'수제 도자기 머그컵 만들기',
     '전문 도예가와 함께 나만의 머그컵을 만드는 워크숍입니다. 초보자도 쉽게 따라할 수 있습니다.',
     5000000,
     'TECH',
     DATEADD('DAY', -5, CURRENT_TIMESTAMP),
     DATEADD('DAY', 25, CURRENT_TIMESTAMP),
     'FUNDING',
     NOW(),
     NOW()),

    (2, 2,'수제 디저트 만들기 클래스',
     '신선한 재료를 사용해 쿠키와 마카롱을 직접 만들어보는 클래스입니다. 초보자도 쉽게 따라 할 수 있으며, 완성된 디저트는 포장해서 선물할 수도 있습니다.',
     3000000,
     'FOOD',
     DATEADD('DAY', -2, CURRENT_TIMESTAMP),
     DATEADD('DAY', 20, CURRENT_TIMESTAMP),
     'FUNDING',
     NOW(),
     NOW());

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
-- { "email": "backer@test.com", "password": "test1234", "name": "후원자" }
-- { "email": "creator@test.com", "password": "test1234", "name": "크리에이터" }
-- { "email": "admin@test.com", "password": "test1234", "name": "관리자" }
-- ===================================
-- 이메일: backer@test.com, creator@test.com, admin@test.com
-- 비밀번호: test1234 (모두 동일)
-- 프로젝트: id=1 (크리에이터 id=2)
-- 리워드: id=1~5 (프로젝트 id=1)
-- ===================================
