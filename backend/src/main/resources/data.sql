INSERT INTO tbl_tag (name) VALUES ('SPRING');
INSERT INTO tbl_tag (name) VALUES ('JPA');
INSERT INTO tbl_tag (name) VALUES ('SECURITY');
INSERT INTO tbl_tag (name) VALUES ('REDIS');
INSERT INTO tbl_tag (name) VALUES ('MYSQL');
INSERT INTO tbl_tag (name) VALUES ('AWS');
INSERT INTO tbl_tag (name) VALUES ('REACT');
INSERT INTO tbl_tag (name) VALUES ('ALGORITHM');
INSERT INTO tbl_tag (name) VALUES ('CS');
INSERT INTO tbl_tag (name) VALUES ('PROJECT');

INSERT INTO tbl_user (
    email,
    password,
    nickname,
    role,
    provider,
    provider_id,
    deleted,
    created_at,
    updated_at
) VALUES (
             'test@test.com',
             '$2a$10$K7NapGTPgkdxfF3CQbkABuhS.lMG5HhVH89SQ7YhwaAWaQGTEUoR.',
             'hi',
             'ROLE_USER',
             'LOCAL',
             NULL,
             false,
             NOW(),
             NOW()
         );

INSERT INTO tbl_study (
    category,
    created_at,
    current_members,
    description,
    end_date,
    leader_id,
    max_members,
    recruit_end_date,
    start_date,
    status,
    title,
    updated_at
)
VALUES
('BACKEND', NOW(), 1, '백엔드 스터디 설명 1', '2024-04-30', 1, 5, '2024-02-01', '2024-02-05', 'OPENED', '백엔드 스터디 1', NOW()),
('FRONTEND', NOW(), 2, '프론트엔드 스터디 설명 2', '2024-04-30', 1, 6, '2024-02-01', '2024-02-05', 'OPENED', '프론트엔드 스터디 2', NOW()),
('ALGORITHM', NOW(), 3, '알고리즘 스터디 설명 3', '2024-04-30', 1, 4, '2024-02-01', '2024-02-05', 'OPENED', '알고리즘 스터디 3', NOW()),
('CS', NOW(), 1, 'CS 스터디 설명 4', '2024-04-30', 1, 5, '2024-02-01', '2024-02-05', 'OPENED', 'CS 스터디 4', NOW()),
('PROJECT', NOW(), 2, '프로젝트 스터디 설명 5', '2024-04-30', 1, 6, '2024-02-01', '2024-02-05', 'CLOSED', '프로젝트 스터디 5', NOW()),
('BACKEND', NOW(), 2, '백엔드 스터디 설명 6', '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'OPENED', '백엔드 스터디 6', NOW()),
('FRONTEND', NOW(), 1, '프론트엔드 스터디 설명 7', '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'OPENED', '프론트엔드 스터디 7', NOW()),
('ALGORITHM', NOW(), 3, '알고리즘 스터디 설명 8', '2024-05-15', 1, 6, '2024-02-10', '2024-02-15', 'OPENED', '알고리즘 스터디 8', NOW()),
('CS', NOW(), 2, 'CS 스터디 설명 9', '2024-05-15', 1, 4, '2024-02-10', '2024-02-15', 'OPENED', 'CS 스터디 9', NOW()),
('PROJECT', NOW(), 1, '프로젝트 스터디 설명 10', '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'OPENED', '프로젝트 스터디 10', NOW()),
('BACKEND', NOW(), 1, '백엔드 스터디 설명 1', '2024-04-30', 1, 5, '2024-02-01', '2024-02-05', 'OPENED', '백엔드 스터디 11', NOW()),
('FRONTEND', NOW(), 2, '프론트엔드 스터디 설명 2', '2024-04-30', 1, 6, '2024-02-01', '2024-02-05', 'OPENED', '프론트엔드 스터디 12', NOW()),
('ALGORITHM', NOW(), 3, '알고리즘 스터디 설명 3', '2024-04-30', 1, 4, '2024-02-01', '2024-02-05', 'OPENED', '알고리즘 스터디 13', NOW()),
('CS', NOW(), 1, 'CS 스터디 설명 4', '2024-04-30', 1, 5, '2024-02-01', '2024-02-05', 'OPENED', 'CS 스터디 14', NOW()),
('PROJECT', NOW(), 2, '프로젝트 스터디 설명 5', '2024-04-30', 1, 6, '2024-02-01', '2024-02-05', 'OPENED', '프로젝트 스터디 15', NOW()),
('BACKEND', NOW(), 2, '백엔드 스터디 설명 6', '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'OPENED', '백엔드 스터디 16', NOW()),
('FRONTEND', NOW(), 1, '프론트엔드 스터디 설명 7', '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'OPENED', '프론트엔드 스터디 17', NOW()),
('ALGORITHM', NOW(), 3, '알고리즘 스터디 설명 8', '2024-05-15', 1, 6, '2024-02-10', '2024-02-15', 'OPENED', '알고리즘 스터디 18', NOW()),
('CS', NOW(), 2, 'CS 스터디 설명 9', '2024-05-15', 1, 4, '2024-02-10', '2024-02-15', 'OPENED', 'CS 스터디 19', NOW()),
('PROJECT', NOW(), 1, '프로젝트 스터디 설명 10', '2024-05-15', 1, 5, '2024-02-10', '2024-02-15', 'CLOSED', '프로젝트 스터디 20', NOW());
