-- ===================================
-- SELECT * FROM users;
-- SELECT * FROM creator_profile;
-- SELECT * FROM creator_wallet;
-- SELECT * FROM project;
-- SELECT * FROM reward;
-- ===================================
INSERT INTO users (id, email, password, name, role, created_at, updated_at)
VALUES (1, 'backer@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '후원자', 'BACKER', NOW(),
        NOW()),
       (2, 'creator@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '크리에이터', 'CREATOR',
        NOW(), NOW()),
       (3, 'admin@test.com', '$2a$10$PIyfq3OWrbkLwkCmxY2yoe7XDCCUXGYeiz6uVn1QVie.PF4lQG48e', '관리자', 'ADMIN', NOW(),
        NOW());

-- 2. 크리에이터 프로필 (user_id=2)
INSERT INTO creator_profile (id, user_id, bank_name, account_number, account_holder, business_number, business_name,
                             created_at, updated_at)
VALUES (1, 2, '카카오뱅크', '3333-12-1234567', '크리에이터', '123-45-67890', '크리에이터 스튜디오', NOW(), NOW());

-- 3. 크리에이터 지갑 (user_id=2)
INSERT INTO creator_wallet (id, user_id, available_balance, pending_balance, total_earned, total_withdrawn, updated_at)
VALUES (1, 2, 0, 0, 0, 0, NOW());

-- 4. 프로젝트 (creator_user_id=2, 현재 펀딩 중)
INSERT INTO project (id, creator_user_id, title, goal_amount, start_at, end_at, status, created_at, updated_at)
VALUES (1, 2, '수제 도자기 머그컵 만들기', 5000000,
        DATEADD('DAY', -5, CURRENT_TIMESTAMP),
        DATEADD('DAY', 25, CURRENT_TIMESTAMP),
        'FUNDING', NOW(), NOW());

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
