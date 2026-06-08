-- 菜单表 + 角色菜单关联（与 init_rbac_data.sql 中 role_id=900001 等配合）
-- 根节点 parent_menu_id 使用空字符串 ''
-- 业务菜单统一挂在 m_sys_manage 下（树形）；m_sys_manage 使用 menu_type=DIR，无路由
-- 若表已存在可重复执行：INSERT 使用 IGNORE；UPDATE 可重复执行

CREATE TABLE IF NOT EXISTS `sys_menus` (
    `menu_id` varchar(60) NOT NULL COMMENT '菜单编号',
    `menu_name` VARCHAR(100) NOT NULL COMMENT '菜单名称',
    `menu_type` VARCHAR(50) NOT NULL COMMENT '菜单类型(URL:连接, DIR:目录)',
    `menu_url` VARCHAR(400) NOT NULL DEFAULT '' COMMENT '菜单类型为url时标记跳转的路径',
    `parent_menu_id` varchar(60) DEFAULT '' COMMENT '父菜单ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`menu_id`),
    KEY `idx_parent_menu_id` (`parent_menu_id`),
    KEY `idx_menu_name` (`menu_name`(100)),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` VARCHAR(60) NOT NULL COMMENT '菜单ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_rm_role` (`role_id`),
    KEY `idx_rm_menu` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联';

-- 根：系统管理（目录，无 view）
INSERT IGNORE INTO `sys_menus`
(`menu_id`, `menu_name`, `menu_type`, `menu_url`, `parent_menu_id`, `status`, `sort_order`, `remark`, `deleted`)
VALUES
('m_sys_manage', '系统管理', 'DIR', '', '', 1, 0, '功能菜单根', 0);

-- 子菜单（menu_url 与 jiabao_front_manage MainView 一致）
INSERT IGNORE INTO `sys_menus`
(`menu_id`, `menu_name`, `menu_type`, `menu_url`, `parent_menu_id`, `status`, `sort_order`, `remark`, `deleted`)
VALUES
('m_org', '组织机构', 'URL', 'org', 'm_sys_manage', 1, 10, '默认', 0),
('m_role', '角色管理', 'URL', 'role', 'm_sys_manage', 1, 20, '默认', 0),
('m_user', '用户管理', 'URL', 'user', 'm_sys_manage', 1, 30, '默认', 0),
('m_student', '学生管理', 'URL', 'student', 'm_sys_manage', 1, 35, '默认', 0),
('m_dict', '字典管理', 'URL', 'dict', 'm_sys_manage', 1, 37, '默认', 0),
('m_cms', '文章管理', 'URL', 'cms', 'm_sys_manage', 1, 40, '默认', 0),
('m_menu', '菜单管理', 'URL', 'menu', 'm_sys_manage', 1, 50, '默认', 0),
('m_role_menu', '角色菜单授权', 'URL', 'role_menu', 'm_sys_manage', 1, 60, '默认', 0);

-- 已有库：把仍挂在根上的功能菜单归到系统管理下
UPDATE `sys_menus`
SET `parent_menu_id` = 'm_sys_manage'
WHERE `menu_id` IN ('m_org', 'm_role', 'm_user', 'm_student', 'm_dict', 'm_cms', 'm_menu', 'm_role_menu')
  AND (`parent_menu_id` = '' OR `parent_menu_id` IS NULL);

-- 系统管理员：含目录根 + 叶子（ROLE_ADMIN 后端仍会放行全部，此处便于数据一致）
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(900001, 'm_sys_manage'),
(900001, 'm_org'),
(900001, 'm_role'),
(900001, 'm_user'),
(900001, 'm_student'),
(900001, 'm_dict'),
(900001, 'm_cms'),
(900001, 'm_menu'),
(900001, 'm_role_menu');

-- 组织管理员
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(900002, 'm_sys_manage'),
(900002, 'm_org'),
(900002, 'm_role'),
(900002, 'm_user'),
(900002, 'm_student'),
(900002, 'm_dict'),
(900002, 'm_cms');

-- 普通用户
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(900003, 'm_sys_manage'),
(900003, 'm_org'),
(900003, 'm_cms');
