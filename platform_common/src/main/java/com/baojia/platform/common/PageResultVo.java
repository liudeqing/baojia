package com.baojia.platform.common;

import lombok.Data;

import java.util.List;

@Data
@SuppressWarnings("all")
public class PageResultVo<T> {

    private Long total;

    private Integer pageNum;

    private Integer pageSize;

    private List<T> records;
}
