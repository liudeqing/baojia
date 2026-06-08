CREATE TABLE `sys_organization` (
    `org_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '组织ID',
    `org_name` VARCHAR(100) NOT NULL COMMENT '组织名称',
    `org_code` VARCHAR(50) NOT NULL COMMENT '组织编码（唯一）',
    `org_type` TINYINT NOT NULL DEFAULT 1 COMMENT '组织类型：1-集团，2-公司，3-部门，4-团队',
    `parent_org_id` BIGINT DEFAULT NULL COMMENT '父组织ID',
    `org_level` INT NOT NULL DEFAULT 1 COMMENT '组织层级（从1开始）',
    `org_path` VARCHAR(500) DEFAULT NULL COMMENT '组织路径（如：/1/2/3）',
    `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
    `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
    `industry` VARCHAR(50) DEFAULT NULL COMMENT '所属行业',
    `employee_count` INT DEFAULT 0 COMMENT '员工数量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `expire_time` DATETIME DEFAULT NULL COMMENT '租户过期时间',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`org_id`),
    UNIQUE KEY `uk_org_code` (`org_code`),
    KEY `idx_parent_org_id` (`parent_org_id`),
    KEY `idx_org_path` (`org_path`(255)),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_parent_org` FOREIGN KEY (`parent_org_id`) REFERENCES `sys_organization` (`org_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织机构表';

CREATE TABLE `sys_user` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `salt` VARCHAR(50) DEFAULT NULL COMMENT '密码盐值',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `org_id` BIGINT NOT NULL COMMENT '所属组织ID',
    `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
    `job_number` VARCHAR(50) DEFAULT NULL COMMENT '工号',
    `user_type` TINYINT NOT NULL DEFAULT 1 COMMENT '用户类型：1-普通用户，2-管理员，3-超级管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用，2-锁定',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `password_update_time` DATETIME DEFAULT NULL COMMENT '密码更新时间',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_job_number` (`job_number`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_phone` (`phone`),
    KEY `idx_email` (`email`),
    CONSTRAINT `fk_user_org` FOREIGN KEY (`org_id`) REFERENCES `sys_organization` (`org_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE `sys_role` (
                            `role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                            `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
                            `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
                            `org_id` BIGINT NOT NULL COMMENT '所属组织ID',
                            `role_type` TINYINT DEFAULT 1 COMMENT '角色类型：1-系统角色，2-自定义角色',
                            `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
                            `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                            `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
                            PRIMARY KEY (`role_id`),
                            UNIQUE KEY `uk_role_code` (`role_code`),
                            KEY `idx_org_id` (`org_id`),
                            CONSTRAINT `fk_role_org` FOREIGN KEY (`org_id`) REFERENCES `sys_organization` (`org_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE `sys_user_role` (
     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
     `user_id` BIGINT NOT NULL COMMENT '用户ID',
     `role_id` BIGINT NOT NULL COMMENT '角色ID',
     `org_id` BIGINT NOT NULL COMMENT '所属组织ID',
     `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
     KEY `idx_user_id` (`user_id`),
     KEY `idx_role_id` (`role_id`),
     KEY `idx_org_id` (`org_id`),
     CONSTRAINT `fk_userrole_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
     CONSTRAINT `fk_userrole_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE,
     CONSTRAINT `fk_userrole_org` FOREIGN KEY (`org_id`) REFERENCES `sys_organization` (`org_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =============================================================================
-- 菜单与角色菜单（详见 resources/menus_init.sql 初始化数据）
-- =============================================================================
CREATE TABLE IF NOT EXISTS `sys_menus` (
    `menu_id` varchar(60) NOT NULL COMMENT '菜单编号',
    `menu_name` VARCHAR(100) NOT NULL COMMENT '菜单名称',
    `menu_type` VARCHAR(50) NOT NULL COMMENT '菜单类型(URL:连接)',
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

-- =============================================================================
-- 学生管理（对应实体 com.baojia.user_manager.model.Student）
-- 状态：0-正常 1-已删除（逻辑删除）
-- =============================================================================
CREATE TABLE IF NOT EXISTS `student` (
    `student_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学生编号',
    `student_name` VARCHAR(512) NOT NULL COMMENT '学生姓名',
    `student_school_name` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '学校名称',
    `student_grade_name` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '年级',
    `student_class_name` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '班级',
    `student_sex_name` VARCHAR(3) NOT NULL DEFAULT '' COMMENT '性别',
    `student_birthday` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '生日',
    `student_link_name` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '学生监护人姓名',
    `student_link_phone` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '学生监护人手机号',
    `student_link_type` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '监护人类型:父亲,母亲',
    `student_parent_name` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '学生父亲姓名',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态;0:正常;1:已删除',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人用户ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`student_id`),
    KEY `idx_student_student_name` (`student_name`(191)),
    KEY `idx_student_link_phone` (`student_link_phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生管理';

-- =============================================================================
-- 字典树（对应实体 com.baojia.platform.dict.model.Dict）
-- 根节点 dict_parent_id = 0；同级别 dict_name 唯一（业务层校验 + 唯一索引）
-- =============================================================================
CREATE TABLE IF NOT EXISTS `dict` (
    `dict_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典编号',
    `dict_name` VARCHAR(50) NOT NULL COMMENT '字典名称',
    `dict_parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父级节点编号，根节点为0',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人用户ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`dict_id`),
    KEY `idx_dict_dict_name` (`dict_name`),
    KEY `idx_dict_parent_id` (`dict_parent_id`),
    UNIQUE KEY `uk_dict_parent_name` (`dict_parent_id`, `dict_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典管理';

-- =============================================================================
-- CMS 内容发布：文章表（对应实体 com.baojia.user_manager.model.CmsArticle）
-- 状态：0-草稿 1-待审核 2-已发布 3-驳回
-- =============================================================================
CREATE TABLE IF NOT EXISTS `cms_article` (
    `article_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章主键',
    `title` VARCHAR(512) NOT NULL COMMENT '标题',
    `category` VARCHAR(128) NOT NULL DEFAULT '其他' COMMENT '栏目（用于前台分组展示）',
    `content_html` MEDIUMTEXT COMMENT '富文本 HTML 正文',
    `gallery_json` TEXT COMMENT '文章配图 URL 列表（JSON 数组字符串）',
    `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '文章封面图 URL',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0草稿 1待审核 2已发布 3驳回',
    `reject_reason` VARCHAR(512) DEFAULT NULL COMMENT '驳回原因',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间（审核通过时写入）',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人用户ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`article_id`),
    KEY `idx_cms_article_category` (`category`),
    KEY `idx_cms_article_status` (`status`),
    KEY `idx_cms_article_published_at` (`published_at`),
    KEY `idx_cms_article_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CMS文章表';


CREATE TABLE IF NOT EXISTS `dict` (
 `dict_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典编号',
 `dict_name` VARCHAR(50) NOT NULL COMMENT '字典名称',
 `dict_parent_id` BIGINT NOT NULL DEFAULT '' COMMENT '父级节点的编号,如果为根节点则设置为0',
 `dict_group` varchar(40) not null default '' comment '字典分组',
 `create_by` BIGINT DEFAULT NULL COMMENT '创建人用户ID',
 `update_by` BIGINT DEFAULT NULL COMMENT '更新人用户ID',
 `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`dict_id`),
 KEY `idx_dict_dict_name` (`dict_name`(50))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典管理';

alter table dict add column dict_group varchar(40) not null default '' comment '字典分组';