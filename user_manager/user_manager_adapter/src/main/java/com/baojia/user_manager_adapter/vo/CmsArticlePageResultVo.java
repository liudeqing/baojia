package com.baojia.user_manager_adapter.vo;

import lombok.Data;

import java.util.List;

@Data
public class CmsArticlePageResultVo<T> {

    private long total;

    private int pageNum;

    private int pageSize;

    private List<T> records;
}
