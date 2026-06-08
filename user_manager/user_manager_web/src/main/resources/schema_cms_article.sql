-- 与 ddd.sql 中 cms_article 定义保持一致；可单独在库 baojia 中执行
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


ALTER TABLE `cms_article`
    ADD COLUMN `cover_url` VARCHAR(512) DEFAULT NULL COMMENT '文章封面图 URL' AFTER `gallery_json`;
ALTER TABLE `cms_article`
    ADD COLUMN `category` VARCHAR(128) NOT NULL DEFAULT '其他' COMMENT '栏目（用于前台分组展示）' AFTER `title`;

CREATE INDEX `idx_cms_article_category` ON `cms_article` (`category`);



CREATE TABLE IF NOT EXISTS `student` (
    `student_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学生编号',
    `student_name` VARCHAR(512) NOT NULL COMMENT '学生姓名',
    `student_school_name` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '学校名称',
    `student_grade_name` VARCHAR(20) not null default '' COMMENT '年级',
    `student_class_name` VARCHAR(20) not null default '' COMMENT '班级',
    `student_sex_name` VARCHAR(3) not null default '' COMMENT '性别',
    `student_birthday` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '生日',
    `student_link_name` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '学生监护人姓名',
    `student_link_phone` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '学生监护人手机号',
    `student_link_type` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '监护人类型:父亲,母亲',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态;Deleted:已删除;Normal:正常',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人用户ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
PRIMARY KEY (`student_id`),
KEY `idx_student_student_name` (`student_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生管理';

CREATE TABLE IF NOT EXISTS `service_item` (
    `service_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '服务编号',
    `service_name` VARCHAR(512) NOT NULL COMMENT '服务套餐名称',
    `service_description` text(128) NOT NULL DEFAULT '' COMMENT '服务描述',
    `service_content_html ` MEDIUMTEXT COMMENT '服务内容富文本',
    `service_cycle` VARCHAR(20) not null default '' COMMENT '班级',
    `student_sex_name` VARCHAR(3) not null default '' COMMENT '性别',
    `student_birthday` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '生日',
    `student_link_name` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '学生监护人姓名',
    `student_link_phone` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '学生监护人手机号',
    `student_link_type` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '监护人类型:父亲,母亲',
    `student_parent_name` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '学生父亲姓名',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态;Deleted:已删除;Normal:正常',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人用户ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`student_id`),
    KEY `idx_student_student_name` (`student_name`),
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生管理';
