package com.baojia.platform.dict.service;

import com.baojia.platform.dict.dto.DictSaveDto;
import com.baojia.platform.dict.vo.DictTreeVo;

import java.util.List;

public interface IDictService {

    List<DictTreeVo> tree();

    void save(DictSaveDto dto, Long operatorUserId);

    void delete(Long dictId, Long operatorUserId);
}
