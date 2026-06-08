package com.baojia.user_manager.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容文章：草稿 → 待审核 → 已发布（或驳回）。
 */
@Data
@TableName("cms_article")
public class CmsArticle {

    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_PUBLISHED = 2;
    public static final int STATUS_REJECTED = 3;

    @TableId(type = IdType.AUTO)
    private Long articleId;

    private String title;

    /** 栏目名称，用于前台按栏目分组展示 */
    private String category;

    /** 富文本 HTML */
    private String contentHtml;

    /** JSON 数组：配图 URL 列表（与正文内嵌图片可同时使用） */
    private String galleryJson;

    /** 封面图 URL（单张） */
    private String coverUrl;

    private Integer status;

    private String rejectReason;

    private LocalDateTime publishedAt;

    private Long createBy;

    private Long updateBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
