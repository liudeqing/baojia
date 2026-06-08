package com.baojia.platform.dict.service.impl;

import com.baojia.platform.dict.dto.DictSaveDto;
import com.baojia.platform.dict.mapper.DictMapper;
import com.baojia.platform.dict.model.Dict;
import com.baojia.platform.dict.service.IDictService;
import com.baojia.platform.dict.vo.DictTreeVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@SuppressWarnings("all")
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

    @Override
    public List<DictTreeVo> tree() {
        List<Dict> all = list(new LambdaQueryWrapper<Dict>().orderByAsc(Dict::getDictId));
        Map<Long, DictTreeVo> idToVo = new HashMap<>();
        for (Dict d : all) {
            DictTreeVo vo = new DictTreeVo();
            vo.setDictId(d.getDictId());
            vo.setDictName(d.getDictName());
            vo.setDictParentId(d.getDictParentId());
            vo.setDictGroup(d.getDictGroup());
            idToVo.put(d.getDictId(), vo);
        }
        List<DictTreeVo> roots = new ArrayList<>();
        for (Dict d : all) {
            DictTreeVo node = idToVo.get(d.getDictId());
            Long pid = d.getDictParentId() == null ? Dict.ROOT_PARENT_ID : d.getDictParentId();
            if (Objects.equals(pid, Dict.ROOT_PARENT_ID)) {
                roots.add(node);
            } else {
                DictTreeVo parent = idToVo.get(pid);
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    roots.add(node);
                }
            }
        }
        sortTree(roots);
        return roots;
    }

    private void sortTree(List<DictTreeVo> nodes) {
        nodes.sort(Comparator.comparing(DictTreeVo::getDictId));
        for (DictTreeVo n : nodes) {
            if (n.getChildren() != null && !n.getChildren().isEmpty()) {
                sortTree(n.getChildren());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DictSaveDto dto, Long operatorUserId) {
        if (dto == null || !StringUtils.hasText(dto.getDictName())) {
            throw new RuntimeException("字典名称不能为空");
        }
        String name = dto.getDictName().trim();
        long parentId = dto.getDictParentId() == null ? Dict.ROOT_PARENT_ID : dto.getDictParentId();

        if (dto.getDictId() == null) {
            assertSiblingNameUnique(parentId, name, null);
            Dict e = new Dict();
            e.setDictName(name);
            e.setDictParentId(parentId);
            e.setCreateBy(operatorUserId);
            e.setUpdateBy(operatorUserId);
            LocalDateTime now = LocalDateTime.now();
            e.setCreateTime(now);
            e.setUpdateTime(now);
            e.setDictGroup( dto.getDictGroup() );
            save(e);
            return;
        }

        Dict existing = getById(dto.getDictId());
        if (existing == null) {
            throw new RuntimeException("字典不存在");
        }
        long effectiveParent = dto.getDictParentId() != null ? dto.getDictParentId() : existing.getDictParentId();
        if (effectiveParent != existing.getDictParentId()) {
            assertNotMoveUnderDescendant(dto.getDictId(), effectiveParent);
        }
        assertSiblingNameUnique(effectiveParent, name, dto.getDictId());
        existing.setDictName(name);
        existing.setDictParentId(effectiveParent);
        existing.setUpdateBy(operatorUserId);
        existing.setUpdateTime(LocalDateTime.now());
        updateById(existing);
    }

    /**
     * 禁止将节点挂到自身或其任意子孙下，避免成环。
     */
    private void assertNotMoveUnderDescendant(long nodeId, long newParentId) {
        if (newParentId == nodeId) {
            throw new RuntimeException("不能将节点挂在自己下面");
        }
        Set<Long> subtree = new HashSet<>();
        Deque<Long> q = new ArrayDeque<>();
        q.add(nodeId);
        while (!q.isEmpty()) {
            Long id = q.poll();
            if (!subtree.add(id)) {
                continue;
            }
            List<Dict> ch = list(new LambdaQueryWrapper<Dict>().eq(Dict::getDictParentId, id));
            for (Dict c : ch) {
                q.add(c.getDictId());
            }
        }
        if (subtree.contains(newParentId)) {
            throw new RuntimeException("不能将节点移动到其子树内");
        }
    }

    private void assertSiblingNameUnique(long parentId, String name, Long excludeDictId) {
        LambdaQueryWrapper<Dict> w = new LambdaQueryWrapper<Dict>()
                .eq(Dict::getDictParentId, parentId)
                .eq(Dict::getDictName, name);
        if (excludeDictId != null) {
            w.ne(Dict::getDictId, excludeDictId);
        }
        if (count(w) > 0) {
            throw new RuntimeException("同级别下已存在相同名称的字典节点");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long dictId, Long operatorUserId) {
        if (getById(dictId) == null) {
            throw new RuntimeException("字典不存在");
        }
        long cnt = count(new LambdaQueryWrapper<Dict>().eq(Dict::getDictParentId, dictId));
        if (cnt > 0) {
            throw new RuntimeException("存在子节点，不能删除");
        }
        removeById(dictId);
    }
}
