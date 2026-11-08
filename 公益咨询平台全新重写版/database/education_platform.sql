CREATE DATABASE IF NOT EXISTS education_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE education_platform;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(40) NOT NULL UNIQUE,
 password VARCHAR(100) NOT NULL, nickname VARCHAR(60) NOT NULL,
 role ENUM('USER','PROFESSOR','ADMIN') NOT NULL DEFAULT 'USER',
 status ENUM('正常','禁用') NOT NULL DEFAULT '正常', avatar VARCHAR(500),
 phone VARCHAR(30), email VARCHAR(100), created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 INDEX idx_user_role_status(role,status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS professor_profile (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL UNIQUE,
 real_name VARCHAR(60) NOT NULL, title VARCHAR(80), institution VARCHAR(120),
 specialty VARCHAR(500), introduction TEXT, audit_status ENUM('待审核','已通过','已拒绝') NOT NULL DEFAULT '待审核',
 audit_remark VARCHAR(500), audited_by BIGINT, audited_at DATETIME,
 FOREIGN KEY(user_id) REFERENCES sys_user(id), INDEX idx_prof_audit(audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS professor_schedule (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, professor_id BIGINT NOT NULL,
 start_time DATETIME NOT NULL, end_time DATETIME NOT NULL,
 capacity INT NOT NULL DEFAULT 1, status ENUM('开放','关闭') NOT NULL DEFAULT '开放',
 FOREIGN KEY(professor_id) REFERENCES sys_user(id), INDEX idx_schedule_time(professor_id,start_time,end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS professor_appointment (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, appointment_no VARCHAR(40) NOT NULL UNIQUE,
 user_id BIGINT NOT NULL, professor_id BIGINT NOT NULL, schedule_id BIGINT,
 consultation_type VARCHAR(60) NOT NULL, subject VARCHAR(200) NOT NULL, content TEXT NOT NULL,
 appointment_time DATETIME NOT NULL,
 status ENUM('待确认','已接受','已拒绝','已取消','已完成') NOT NULL DEFAULT '待确认',
 professor_reply TEXT, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 FOREIGN KEY(user_id) REFERENCES sys_user(id), FOREIGN KEY(professor_id) REFERENCES sys_user(id),
 FOREIGN KEY(schedule_id) REFERENCES professor_schedule(id),
 INDEX idx_appointment_user(user_id,status), INDEX idx_appointment_prof(professor_id,status), INDEX idx_appointment_time(appointment_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS consultation_record (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, appointment_id BIGINT NOT NULL UNIQUE,
 user_id BIGINT NOT NULL, professor_id BIGINT NOT NULL, summary TEXT, feedback TEXT,
 completed_at DATETIME, FOREIGN KEY(appointment_id) REFERENCES professor_appointment(id),
 FOREIGN KEY(user_id) REFERENCES sys_user(id), FOREIGN KEY(professor_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS resource_category (id BIGINT PRIMARY KEY AUTO_INCREMENT,name VARCHAR(80) NOT NULL UNIQUE,sort_no INT DEFAULT 0) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS learning_resource (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,title VARCHAR(200) NOT NULL,category_id BIGINT,description TEXT,
 file_url VARCHAR(500),cover_url VARCHAR(500),uploader_id BIGINT,downloads INT NOT NULL DEFAULT 0,
 audit_status ENUM('待审核','已发布','已下架','已拒绝') NOT NULL DEFAULT '待审核',created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(category_id) REFERENCES resource_category(id),FOREIGN KEY(uploader_id) REFERENCES sys_user(id),INDEX idx_resource_status(audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS user_favorite (id BIGINT PRIMARY KEY AUTO_INCREMENT,user_id BIGINT NOT NULL,target_type VARCHAR(30) NOT NULL,target_id BIGINT NOT NULL,created_at DATETIME DEFAULT CURRENT_TIMESTAMP,UNIQUE KEY uk_favorite(user_id,target_type,target_id),FOREIGN KEY(user_id) REFERENCES sys_user(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS platform_article (id BIGINT PRIMARY KEY AUTO_INCREMENT,title VARCHAR(200) NOT NULL,summary VARCHAR(500),content LONGTEXT,article_type ENUM('新闻','公告') NOT NULL DEFAULT '新闻',cover_url VARCHAR(500),status ENUM('草稿','已发布','已下架') NOT NULL DEFAULT '草稿',publisher_id BIGINT,created_at DATETIME DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(publisher_id) REFERENCES sys_user(id),INDEX idx_article_status(status,created_at)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS study_plan (id BIGINT PRIMARY KEY AUTO_INCREMENT,user_id BIGINT NOT NULL,title VARCHAR(200) NOT NULL,description TEXT,start_date DATE,end_date DATE,target_days INT DEFAULT 0,completed_days INT DEFAULT 0,status ENUM('进行中','已完成','已暂停') DEFAULT '进行中',created_at DATETIME DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(user_id) REFERENCES sys_user(id),INDEX idx_plan_user(user_id,status)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS study_checkin (id BIGINT PRIMARY KEY AUTO_INCREMENT,plan_id BIGINT NOT NULL,user_id BIGINT NOT NULL,checkin_date DATE NOT NULL,note VARCHAR(500),created_at DATETIME DEFAULT CURRENT_TIMESTAMP,UNIQUE KEY uk_checkin(plan_id,checkin_date),FOREIGN KEY(plan_id) REFERENCES study_plan(id),FOREIGN KEY(user_id) REFERENCES sys_user(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS community_post (id BIGINT PRIMARY KEY AUTO_INCREMENT,user_id BIGINT NOT NULL,title VARCHAR(200) NOT NULL,content TEXT NOT NULL,status ENUM('正常','隐藏','删除') DEFAULT '正常',like_count INT DEFAULT 0,created_at DATETIME DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(user_id) REFERENCES sys_user(id),INDEX idx_post_status(status,created_at)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS community_comment (id BIGINT PRIMARY KEY AUTO_INCREMENT,post_id BIGINT NOT NULL,user_id BIGINT NOT NULL,parent_id BIGINT,content TEXT NOT NULL,status ENUM('正常','隐藏','删除') DEFAULT '正常',created_at DATETIME DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(post_id) REFERENCES community_post(id),FOREIGN KEY(user_id) REFERENCES sys_user(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS community_like (id BIGINT PRIMARY KEY AUTO_INCREMENT,post_id BIGINT NOT NULL,user_id BIGINT NOT NULL,created_at DATETIME DEFAULT CURRENT_TIMESTAMP,UNIQUE KEY uk_post_like(post_id,user_id),FOREIGN KEY(post_id) REFERENCES community_post(id),FOREIGN KEY(user_id) REFERENCES sys_user(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS study_question (id BIGINT PRIMARY KEY AUTO_INCREMENT,user_id BIGINT NOT NULL,title VARCHAR(200) NOT NULL,content TEXT NOT NULL,status ENUM('待回答','已解决','已关闭') DEFAULT '待回答',accepted_answer_id BIGINT,created_at DATETIME DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(user_id) REFERENCES sys_user(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS question_answer (id BIGINT PRIMARY KEY AUTO_INCREMENT,question_id BIGINT NOT NULL,user_id BIGINT NOT NULL,content TEXT NOT NULL,is_accepted TINYINT(1) DEFAULT 0,created_at DATETIME DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(question_id) REFERENCES study_question(id),FOREIGN KEY(user_id) REFERENCES sys_user(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS user_message (id BIGINT PRIMARY KEY AUTO_INCREMENT,user_id BIGINT NOT NULL,title VARCHAR(200) NOT NULL,content TEXT NOT NULL,message_type VARCHAR(40),is_read TINYINT(1) DEFAULT 0,created_at DATETIME DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(user_id) REFERENCES sys_user(id),INDEX idx_message_user(user_id,is_read,created_at)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS service_evaluation (id BIGINT PRIMARY KEY AUTO_INCREMENT,appointment_id BIGINT NOT NULL UNIQUE,user_id BIGINT NOT NULL,professor_id BIGINT NOT NULL,rating TINYINT NOT NULL,content VARCHAR(1000),created_at DATETIME DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY(appointment_id) REFERENCES professor_appointment(id),FOREIGN KEY(user_id) REFERENCES sys_user(id),FOREIGN KEY(professor_id) REFERENCES sys_user(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS banner (id BIGINT PRIMARY KEY AUTO_INCREMENT,title VARCHAR(120),image_url VARCHAR(500) NOT NULL,link_url VARCHAR(500),sort_no INT DEFAULT 0,status ENUM('启用','停用') DEFAULT '启用') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS sys_role (id BIGINT PRIMARY KEY AUTO_INCREMENT,role_code VARCHAR(30) NOT NULL UNIQUE,role_name VARCHAR(60) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS sys_permission (id BIGINT PRIMARY KEY AUTO_INCREMENT,permission_code VARCHAR(100) NOT NULL UNIQUE,permission_name VARCHAR(100) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS sys_role_permission (role_id BIGINT NOT NULL,permission_id BIGINT NOT NULL,PRIMARY KEY(role_id,permission_id),FOREIGN KEY(role_id) REFERENCES sys_role(id),FOREIGN KEY(permission_id) REFERENCES sys_permission(id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS operation_log (id BIGINT PRIMARY KEY AUTO_INCREMENT,user_id BIGINT,username VARCHAR(40),operation VARCHAR(200) NOT NULL,request_method VARCHAR(10),request_uri VARCHAR(300),ip_address VARCHAR(60),created_at DATETIME DEFAULT CURRENT_TIMESTAMP,INDEX idx_log_time(created_at)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO resource_category(id,name,sort_no) VALUES (1,'学习方法',1),(2,'升学指导',2),(3,'职业规划',3),(4,'心理成长',4),(5,'学科资料',5);
INSERT IGNORE INTO sys_role(id,role_code,role_name) VALUES (1,'USER','普通用户'),(2,'PROFESSOR','教授'),(3,'ADMIN','管理员');
-- 默认管理员：账号 admin；密码请首次登录前自行设置 BCrypt 密文，禁止使用明文密码。
-- 可通过后端管理脚本生成 BCrypt 密文后写入 sys_user。
