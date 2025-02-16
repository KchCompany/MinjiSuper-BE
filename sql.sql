use

CREATE TABLE member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE, -- 사용자 ID
    password VARCHAR(255) NOT NULL,       -- 암호화된 비밀번호
    role VARCHAR(20) NOT NULL,            -- 권한 (e.g., 'ROLE_ADMIN')
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 삭제 예정
);

CREATE TABLE post (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title TEXT NOT NULL,
    content LONGTEXT NOT NULL,
    thumbnail VARCHAR(512) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    member_id INT NOT NULL, -- 작성자 ID (외래키)
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

-- 관리자 계정 추가
INSERT INTO member (username, password, role)
VALUES ('kchcompany', '$2a$10$6KlMPEe0Rb5kD/BKN0g6CuvJOGEeqY1aXB/7pH9ZDyTihAo5yFZdy', 'ROLE_ADMIN');
