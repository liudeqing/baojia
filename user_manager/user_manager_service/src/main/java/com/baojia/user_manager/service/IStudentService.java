package com.baojia.user_manager.service;

import com.baojia.platform.common.PageResultVo;
import com.baojia.user_manager.model.Student;
import com.baojia.user_manager_adapter.dto.StudentPageQueryDto;
import com.baojia.user_manager_adapter.dto.StudentSaveDto;
import com.baojia.user_manager_adapter.vo.StudentVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IStudentService extends IService<Student> {

    StudentVo saveStudent(StudentSaveDto dto, Long operatorUserId);

    PageResultVo<StudentVo> pageStudents(StudentPageQueryDto query);

    StudentVo getDetail(Long studentId);

    void logicalDelete(Long studentId, Long operatorUserId);
}
