package com.baojia.user_manager_adapter.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CmsArticleAdminVo {

    private Long articleId;

    private String title;

    private String category;

    private String contentHtml;

    private List<String> galleryUrls;

    private String coverUrl;

    private Integer status;

    private String statusLabel;

    private String rejectReason;

    private LocalDateTime publishedAt;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
