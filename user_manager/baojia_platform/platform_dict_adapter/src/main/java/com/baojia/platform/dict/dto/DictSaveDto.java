package com.baojia.platform.dict.dto;

import lombok.Data;

@Data
public class DictSaveDto {

    /** 修改时必填 */
    private Long dictId;

    private String dictName;

    /** 根节点下新增时为 0 */
    private Long dictParentId;

    private String dictGroup;
}
