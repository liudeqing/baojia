package com.baojia.platform.dict.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DictTreeVo {

    private Long dictId;

    private String dictName;

    private Long dictParentId;

    private String dictGroup;

    private List<DictTreeVo> children = new ArrayList<>();
}
