-- RBAC 默认演示数据（用户 / 组织 / 角色）
-- 前置：已执行 ddd.sql 创建表 sys_organization、sys_user、sys_role、sys_user_role
-- 适用：MySQL 8+
--
-- 默认可登录账号（明文密码均为 123456）：
--   admin   / 123456  （总部 · 系统管理员）
--   jiabao  / 123456  （上海分部 · 普通用户，用于验证「挂在子组织下」的登录）
--   east_manager / 123456  （华东大区 · 组织管理员）
--   sh_user / 123456  （上海分部）
--   hz_user / 123456  （杭州分部）
--
-- 密码哈希：Spring BCryptPasswordEncoder，与后端 SysUserServiceImpl 校验一致
-- 哈希生成命令示例（可选）：见项目 README 或下方注释中的 openssl/ Java 方式

START TRANSACTION;

-- 先删子表再删主表；组织按子→父顺序删，避免外键冲突
DELETE FROM sys_user_role WHERE id BETWEEN 900001 AND 900100;
DELETE FROM sys_user WHERE user_id BETWEEN 900001 AND 900100;
DELETE FROM sys_role WHERE role_id BETWEEN 900001 AND 900100;
DELETE FROM sys_organization WHERE org_id IN (900003, 900004, 900002, 900001);

-- ----------------------------
-- 1) 组织：总部 -> 华东大区 -> 上海分部 / 杭州分部
-- 根组织 parent_org_id 必须为 NULL（不可为 0，否则不满足外键）
-- ----------------------------
INSERT INTO sys_organization
(org_id, org_name, org_code, org_type, parent_org_id, org_level, org_path, contact_person, contact_phone, contact_email, address, industry, employee_count, status, expire_time, sort_order, remark, create_time, update_time, create_by, update_by, deleted)
VALUES
(900001, '总部', 'ORG_HQ', 1, NULL, 1, '/900001', '系统管理员', '13800000001', 'hq@baojia.com', '上海市静安区', '互联网', 0, 1, NULL, 1, '默认初始化', NOW(), NOW(), 1, 1, 0),
(900002, '华东大区', 'ORG_EAST', 2, 900001, 2, '/900001/900002', '区域管理员', '13800000002', 'east@baojia.com', '上海市长宁区', '互联网', 0, 1, NULL, 2, '默认初始化', NOW(), NOW(), 1, 1, 0),
(900003, '上海分部', 'ORG_SH', 3, 900002, 3, '/900001/900002/900003', '上海负责人', '13800000003', 'sh@baojia.com', '上海市浦东新区', '互联网', 0, 1, NULL, 3, '默认初始化', NOW(), NOW(), 1, 1, 0),
(900004, '杭州分部', 'ORG_HZ', 3, 900002, 3, '/900001/900002/900004', '杭州负责人', '13800000004', 'hz@baojia.com', '杭州市西湖区', '互联网', 0, 1, NULL, 4, '默认初始化', NOW(), NOW(), 1, 1, 0);

-- ----------------------------
-- 2) 角色
-- ----------------------------
INSERT INTO sys_role
(role_id, role_name, role_code, org_id, role_type, status, remark, create_time, update_time, deleted)
VALUES
(900001, '系统管理员', 'ROLE_ADMIN', 900001, 1, 1, '默认初始化', NOW(), NOW(), 0),
(900002, '组织管理员', 'ROLE_ORG_ADMIN', 900002, 1, 1, '默认初始化', NOW(), NOW(), 0),
(900003, '普通用户', 'ROLE_USER', 900001, 1, 1, '默认初始化', NOW(), NOW(), 0);

-- ----------------------------
-- 3) 用户（password 对应明文 123456，BCrypt 强度 10）
-- ----------------------------
INSERT INTO sys_user
(user_id, username, password, salt, real_name, nickname, email, phone, avatar, gender, birthday, org_id, position, job_number, user_type, status, last_login_time, last_login_ip, password_update_time, sort_order, remark, create_time, update_time, create_by, update_by, deleted)
VALUES
(900001, 'admin', '$2a$10$zQ6wyCHk.b/M/lz3DvgMPOz0fhJyeuKxEzD1B0XtoOCHuGaAstqSu', NULL, '系统管理员', 'admin', 'admin-demo@jiabao.com', '13900000001', NULL, 1, '1990-01-01', 900001, '平台管理员', 'JB-A0001', 1, 1, NULL, NULL, NOW(), 1, '默认初始化', NOW(), NOW(), 1, 1, 0),
(900002, 'east_manager', '$2a$10$zQ6wyCHk.b/M/lz3DvgMPOz0fhJyeuKxEzD1B0XtoOCHuGaAstqSu', NULL, '华东管理员', 'east', 'east-demo@jiabao.com', '13900000002', NULL, 1, '1992-02-02', 900002, '区域经理', 'JB-A0002', 1, 1, NULL, NULL, NOW(), 2, '默认初始化', NOW(), NOW(), 1, 1, 0),
(900003, 'sh_user', '$2a$10$zQ6wyCHk.b/M/lz3DvgMPOz0fhJyeuKxEzD1B0XtoOCHuGaAstqSu', NULL, '上海员工', 'sh', 'sh-demo@jiabao.com', '13900000003', NULL, 1, '1995-03-03', 900003, '销售', 'JB-A0003', 1, 1, NULL, NULL, NOW(), 3, '默认初始化', NOW(), NOW(), 1, 1, 0),
(900004, 'hz_user', '$2a$10$zQ6wyCHk.b/M/lz3DvgMPOz0fhJyeuKxEzD1B0XtoOCHuGaAstqSu', NULL, '杭州员工', 'hz', 'hz-demo@jiabao.com', '13900000004', NULL, 2, '1996-04-04', 900004, '运营', 'JB-A0004', 1, 1, NULL, NULL, NOW(), 4, '默认初始化', NOW(), NOW(), 1, 1, 0),
(900005, 'jiabao', '$2a$10$zQ6wyCHk.b/M/lz3DvgMPOz0fhJyeuKxEzD1B0XtoOCHuGaAstqSu', NULL, '家宝演示', 'jiabao', 'jiabao-demo@jiabao.com', '13900000005', NULL, 1, '1998-05-05', 900003, '演示账号', 'JB-A0005', 1, 1, NULL, NULL, NOW(), 5, '上海分部演示账号', NOW(), NOW(), 1, 1, 0);

-- ----------------------------
-- 4) 用户角色关联
-- ----------------------------
INSERT INTO sys_user_role
(id, user_id, role_id, org_id, create_time)
VALUES
(900001, 900001, 900001, 900001, NOW()),
(900002, 900002, 900002, 900002, NOW()),
(900003, 900003, 900003, 900003, NOW()),
(900004, 900004, 900003, 900004, NOW()),
(900005, 900005, 900003, 900003, NOW());

COMMIT;
