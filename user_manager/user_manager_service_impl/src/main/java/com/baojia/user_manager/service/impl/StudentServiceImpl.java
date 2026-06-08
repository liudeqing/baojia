package com.baojia.user_manager.service.impl;

import com.baojia.platform.common.PageResultVo;
import com.baojia.user_manager.mapper.StudentMapper;
import com.baojia.user_manager.model.Student;
import com.baojia.user_manager.service.IStudentService;
import com.baojia.user_manager_adapter.dto.StudentPageQueryDto;
import com.baojia.user_manager_adapter.dto.StudentSaveDto;
import com.baojia.user_manager_adapter.vo.StudentVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentVo saveStudent(StudentSaveDto dto, Long operatorUserId) {
        if (dto == null || !StringUtils.hasText(dto.getStudentName())) {
            throw new RuntimeException("学生姓名不能为空");
        }
        Student entity;
        LocalDateTime now = LocalDateTime.now();
        if (dto.getStudentId() == null) {
            entity = new Student();
            entity.setStatus(Student.STATUS_NORMAL);
            entity.setCreateBy(operatorUserId);
            entity.setCreateTime(now);
        } else {
            entity = getById(dto.getStudentId());
            if (entity == null) {
                throw new RuntimeException("学生不存在");
            }
            if (Objects.equals(entity.getStatus(), Student.STATUS_DELETED)) {
                throw new RuntimeException("已删除的学生不可修改");
            }
        }
        entity.setStudentName(dto.getStudentName().trim());
        entity.setStudentSchoolName(trimOrEmpty(dto.getStudentSchoolName()));
        entity.setStudentGradeName(trimOrEmpty(dto.getStudentGradeName()));
        entity.setStudentClassName(trimOrEmpty(dto.getStudentClassName()));
        entity.setStudentSexName(trimOrEmpty(dto.getStudentSexName()));
        entity.setStudentBirthday(trimOrEmpty(dto.getStudentBirthday()));
        entity.setStudentLinkName(trimOrEmpty(dto.getStudentLinkName()));
        entity.setStudentLinkPhone(trimOrEmpty(dto.getStudentLinkPhone()));
        entity.setStudentLinkType(trimOrEmpty(dto.getStudentLinkType()));
        entity.setStudentParentName(trimOrEmpty(dto.getStudentParentName()));
        entity.setUpdateBy(operatorUserId);
        entity.setUpdateTime(now);
        if (dto.getStudentId() == null) {
            save(entity);
        } else {
            updateById(entity);
        }
        return toVo(getById(entity.getStudentId()));
    }

    @Override
    public PageResultVo<StudentVo> pageStudents(StudentPageQueryDto query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : Math.min(query.getPageSize(), 100);
        LambdaQueryWrapper<Student> w = new LambdaQueryWrapper<Student>()
                .eq(Student::getStatus, Student.STATUS_NORMAL);
        if ( StringUtils.hasText(query.getStudentName()) ){
            w.like(Student::getStudentName, query.getStudentName().trim());
        }
        if ( StringUtils.hasText(query.getStudentLinkPhone()) ){
            w.like(StringUtils.hasText(query.getStudentLinkPhone()), Student::getStudentLinkPhone, query.getStudentLinkPhone().trim());
        }
        w.orderByDesc(Student::getUpdateTime);
        Page<Student> page = page(new Page<>(pageNum, pageSize), w);
        PageResultVo<StudentVo> vo = new PageResultVo<>();
        vo.setTotal(page.getTotal());
        vo.setPageNum(pageNum);
        vo.setPageSize(pageSize);
        vo.setRecords(page.getRecords().stream().map(this::toVo).toList());
        return vo;
    }

    @Override
    public StudentVo getDetail(Long studentId) {
        Student s = getById(studentId);
        if (s == null) {
            throw new RuntimeException("学生不存在");
        }
        return toVo(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logicalDelete(Long studentId, Long operatorUserId) {
        Student s = getById(studentId);
        if (s == null) {
            throw new RuntimeException("学生不存在");
        }
        if (Objects.equals(s.getStatus(), Student.STATUS_DELETED)) {
            return;
        }
        s.setStatus(Student.STATUS_DELETED);
        s.setUpdateBy(operatorUserId);
        s.setUpdateTime(LocalDateTime.now());
        updateById(s);
    }

    private static String trimOrEmpty(String v) {
        return v == null ? "" : v.trim();
    }

    private StudentVo toVo(Student s) {
        StudentVo vo = new StudentVo();
        vo.setStudentId(s.getStudentId());
        vo.setStudentName(s.getStudentName());
        vo.setStudentSchoolName(s.getStudentSchoolName());
        vo.setStudentGradeName(s.getStudentGradeName());
        vo.setStudentClassName(s.getStudentClassName());
        vo.setStudentSexName(s.getStudentSexName());
        vo.setStudentBirthday(s.getStudentBirthday());
        vo.setStudentLinkName(s.getStudentLinkName());
        vo.setStudentLinkPhone(s.getStudentLinkPhone());
        vo.setStudentLinkType(s.getStudentLinkType());
        vo.setStudentParentName(s.getStudentParentName());
        vo.setStatus(s.getStatus());
        vo.setStatusLabel(Objects.equals(s.getStatus(), Student.STATUS_DELETED) ? "已删除" : "正常");
        vo.setCreateBy(s.getCreateBy());
        vo.setUpdateBy(s.getUpdateBy());
        vo.setCreateTime(s.getCreateTime());
        vo.setUpdateTime(s.getUpdateTime());
        return vo;
    }
}
