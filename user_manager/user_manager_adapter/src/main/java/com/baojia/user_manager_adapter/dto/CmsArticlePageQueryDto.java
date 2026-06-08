package com.baojia.user_manager_adapter.dto;

import lombok.Data;

@Data
public class CmsArticlePageQueryDto {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 可选：按状态筛选，null 表示全部 */
    private Integer status;
}
