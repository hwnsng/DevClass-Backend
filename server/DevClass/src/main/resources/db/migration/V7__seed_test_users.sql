-- 테스트 계정 삽입 (비밀번호: 1234, BCrypt 해시)
-- 이미 존재하는 경우 스킵
INSERT INTO users (email, password, name, role, status, created_at, updated_at)
SELECT 'admin@admin.com',
       '$2a$10$yY2qLghlU3YUPNfAMH1yleqTwxGEx76k4Z8c/v3uL9BJ1VgRmvea.',
       '관리자',
       'ADMIN',
       'ACTIVE',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@admin.com');

INSERT INTO users (email, password, name, role, status, created_at, updated_at)
SELECT 'teacher@teacher.com',
       '$2a$10$yY2qLghlU3YUPNfAMH1yleqTwxGEx76k4Z8c/v3uL9BJ1VgRmvea.',
       '강사',
       'INSTRUCTOR',
       'ACTIVE',
       NOW(),
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'teacher@teacher.com');
