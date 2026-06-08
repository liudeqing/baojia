package com.baojia.user_manager_adapter.dto;

import lombok.Data;

import java.util.List;

@Data
public class CmsArticleSaveDto {

    private Long articleId;

    private String title;

    /** 栏目，如「产品介绍」「解决方案」；空则保存为「其他」 */
    private String category;

    private String contentHtml;

    /** 文章配图 URL 列表，服务端存为 JSON */
    private List<String> galleryUrls;

    /** 封面图 URL，空则清空 */
    private String coverUrl;
}
