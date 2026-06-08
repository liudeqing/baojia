package com.baojia.user_manager.controller;

import com.baojia.platform.common.ResultModuleVo;
import com.baojia.platform.dict.dto.DictSaveDto;
import com.baojia.platform.dict.service.IDictService;
import com.baojia.platform.dict.vo.DictTreeVo;
import com.baojia.user_manager.security.CurrentUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典树：查询整树、保存节点（同级名称唯一）、删除（无子节点时可删）。
 */
@RestController
@RequestMapping("/body/dict")
@RequiredArgsConstructor
@SuppressWarnings("all")
public class DictController {

    private final IDictService dictService;

    @GetMapping("/tree")
    public ResultModuleVo<List<DictTreeVo>> tree() {
        try {
            return ResultModuleVo.success(dictService.tree());
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResultModuleVo<Boolean> save(@RequestBody DictSaveDto dto) {
        try {
            Long uid = CurrentUserDetail.userIdOrNull();
            dictService.save(dto, uid);
            return ResultModuleVo.success(true);
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResultModuleVo<Boolean> delete(@PathVariable("id") Long id) {
        try {
            Long uid = CurrentUserDetail.userIdOrNull();
            dictService.delete(id, uid);
            return ResultModuleVo.success(true);
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }
}
