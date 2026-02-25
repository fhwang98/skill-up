-- 테스트용 유저 (leader_id = 1 참조용)
INSERT INTO tbl_user (email, nickname, password, provider, role, deleted)
VALUES ('test@skillup.com', '테스트유저', '$2a$10$dummy.password.hash.for.dev.only', 'LOCAL', 'ROLE_USER', false);

INSERT INTO tbl_tag (name) VALUES ('SPRING');
INSERT INTO tbl_tag (name) VALUES ('JPA');
INSERT INTO tbl_tag (name) VALUES ('SECURITY');
INSERT INTO tbl_tag (name) VALUES ('REDIS');
INSERT INTO tbl_tag (name) VALUES ('MYSQL');
INSERT INTO tbl_tag (name) VALUES ('AWS');
INSERT INTO tbl_tag (name) VALUES ('REACT');
INSERT INTO tbl_tag (name) VALUES ('알고리즘');
INSERT INTO tbl_tag (name) VALUES ('컴퓨터구조');
INSERT INTO tbl_tag (name) VALUES ('프로젝트');
INSERT INTO tbl_tag (name) VALUES ('JAVA');
INSERT INTO tbl_tag (name) VALUES ('SPRING_BOOT');
INSERT INTO tbl_tag (name) VALUES ('SPRING_SECURITY');
INSERT INTO tbl_tag (name) VALUES ('JWT');
INSERT INTO tbl_tag (name) VALUES ('OAUTH2');
INSERT INTO tbl_tag (name) VALUES ('DOCKER');
INSERT INTO tbl_tag (name) VALUES ('KUBERNETES');
INSERT INTO tbl_tag (name) VALUES ('CI_CD');
INSERT INTO tbl_tag (name) VALUES ('LINUX');
INSERT INTO tbl_tag (name) VALUES ('네트워크');



INSERT INTO tbl_study (
    category,
    created_at,
    current_members,
    deleted,
    description,
    end_date,
    leader_id,
    max_members,
    recruit_end_date,
    start_date,
    status,
    title,
    updated_at,
    view_count
)
VALUES
('BACKEND',   NOW(), 1, false, '백엔드 스터디 설명 1',      '2024-04-30', 1, 5, '2024-02-01', '2024-02-05', 'OPENED', '백엔드 스터디 1',      NOW(), 0),
('FRONTEND',  NOW(), 2, false, '프론트엔드 스터디 설명 2',  '2024-04-30', 1, 6, '2024-02-01', '2024-02-05', 'OPENED', '프론트엔드 스터디 2',  NOW(), 0),
('ALGORITHM', NOW(), 3, false, '알고리즘 스터디 설명 3',    '2024-04-30', 1, 4, '2024-02-01', '2024-02-05', 'OPENED', '알고리즘 스터디 3',    NOW(), 0),
('CS',        NOW(), 1, false, 'CS 스터디 설명 4',          '2024-04-30', 1, 5, '2024-02-01', '2024-02-05', 'OPENED', 'CS 스터디 4',          NOW(), 0),
('PROJECT',   NOW(), 2, false, '프로젝트 스터디 설명 5',    '2024-04-30', 1, 6, '2024-02-01', '2024-02-05', 'CLOSED', '프로젝트 스터디 5',    NOW(), 0),
('BACKEND',   NOW(), 2, false, '백엔드 스터디 설명 6',      '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'OPENED', '백엔드 스터디 6',      NOW(), 0),
('FRONTEND',  NOW(), 1, false, '프론트엔드 스터디 설명 7',  '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'OPENED', '프론트엔드 스터디 7',  NOW(), 0),
('ALGORITHM', NOW(), 3, false, '알고리즘 스터디 설명 8',    '2024-05-15', 1, 6, '2024-02-10', '2024-02-15', 'OPENED', '알고리즘 스터디 8',    NOW(), 0),
('CS',        NOW(), 2, false, 'CS 스터디 설명 9',          '2024-05-15', 1, 4, '2024-02-10', '2024-02-15', 'OPENED', 'CS 스터디 9',          NOW(), 0),
('PROJECT',   NOW(), 1, false, '프로젝트 스터디 설명 10',   '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'OPENED', '프로젝트 스터디 10',   NOW(), 0),
('BACKEND',   NOW(), 1, false, '백엔드 스터디 설명 1',      '2024-04-30', 1, 5, '2024-02-01', '2024-02-05', 'OPENED', '백엔드 스터디 11',     NOW(), 0),
('FRONTEND',  NOW(), 2, false, '프론트엔드 스터디 설명 2',  '2024-04-30', 1, 6, '2024-02-01', '2024-02-05', 'OPENED', '프론트엔드 스터디 12', NOW(), 0),
('ALGORITHM', NOW(), 3, false, '알고리즘 스터디 설명 3',    '2024-04-30', 1, 4, '2024-02-01', '2024-02-05', 'OPENED', '알고리즘 스터디 13',   NOW(), 0),
('CS',        NOW(), 1, false, 'CS 스터디 설명 4',          '2024-04-30', 1, 5, '2024-02-01', '2024-02-05', 'OPENED', 'CS 스터디 14',         NOW(), 0),
('PROJECT',   NOW(), 2, false, '프로젝트 스터디 설명 5',    '2024-04-30', 1, 6, '2024-02-01', '2024-02-05', 'OPENED', '프로젝트 스터디 15',   NOW(), 0),
('BACKEND',   NOW(), 2, false, '백엔드 스터디 설명 6',      '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'OPENED', '백엔드 스터디 16',     NOW(), 0),
('FRONTEND',  NOW(), 1, false, '프론트엔드 스터디 설명 7',  '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'OPENED', '프론트엔드 스터디 17', NOW(), 0),
('ALGORITHM', NOW(), 3, false, '알고리즘 스터디 설명 8',    '2024-05-15', 1, 6, '2024-02-10', '2024-02-15', 'OPENED', '알고리즘 스터디 18',   NOW(), 0),
('CS',        NOW(), 2, false, 'CS 스터디 설명 9',          '2024-05-15', 1, 4, '2024-02-10', '2024-02-15', 'OPENED', 'CS 스터디 19',         NOW(), 0),
('PROJECT',   NOW(), 1, false, '프로젝트 스터디 설명 10',   '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'CLOSED', '프로젝트 스터디 20',   NOW(), 0);
