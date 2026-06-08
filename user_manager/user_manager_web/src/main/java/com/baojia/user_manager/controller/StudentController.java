package com.baojia.user_manager.controller;

import com.baojia.platform.common.PageResultVo;
import com.baojia.platform.common.ResultModuleVo;
import com.baojia.user_manager.security.CurrentUserDetail;
import com.baojia.user_manager.service.IStudentService;
import com.baojia.user_manager_adapter.dto.StudentPageQueryDto;
import com.baojia.user_manager_adapter.dto.StudentSaveDto;
import com.baojia.user_manager_adapter.vo.StudentVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 学生管理：分页查询（姓名、监护人手机号）、保存、详情、逻辑删除。
 */
@RestController
@RequestMapping("/body/student")
@RequiredArgsConstructor
@SuppressWarnings("all")
public class StudentController {

    private final IStudentService studentService;

    @PostMapping("/save")
    public ResultModuleVo<StudentVo> save(@RequestBody StudentSaveDto dto) {
        try {
            Long uid = CurrentUserDetail.userIdOrNull();
            return ResultModuleVo.success(studentService.saveStudent(dto, uid));
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }

    @PostMapping("/page")
    public ResultModuleVo<PageResultVo<StudentVo>> page(@RequestBody StudentPageQueryDto query) {
        try {
            return ResultModuleVo.success(studentService.pageStudents(query));
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResultModuleVo<StudentVo> detail(@PathVariable("id") Long id) {
        try {
            return ResultModuleVo.success(studentService.getDetail(id));
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResultModuleVo<Boolean> delete(@PathVariable("id") Long id) {
        try {
            Long uid = CurrentUserDetail.userIdOrNull();
            studentService.logicalDelete(id, uid);
            return ResultModuleVo.success(true);
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }
}
