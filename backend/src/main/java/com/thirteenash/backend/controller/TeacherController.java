package com.thirteenash.backend.controller;

import com.thirteenash.backend.entity.Teacher;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.result.Result;
import com.thirteenash.backend.service.TeacherService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 教师管理控制器
 * 提供教师的增删改查 RESTful 接口
 */
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * 分页查询教师列表
     * @param page 页码（可选，默认第1页）
     * @param pageSize 每页条数（可选，默认10条）
     * @param keyword 搜索关键字（可选，按工号或姓名模糊查询）
     * @param gender 性别筛选（可选，"男"或"女"）
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult> page(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String gender) {
        return Result.success(teacherService.page(page, pageSize, keyword, gender));
    }

    /**
     * 新增教师
     * @param teacher 教师信息（请求体）
     * @return 新增后的教师信息
     */
    @PostMapping
    public Result<Teacher> add(@RequestBody Teacher teacher) {
        return Result.success(teacherService.add(teacher));
    }

    /**
     * 根据ID查询教师详情
     * @param teacherId 教师ID（路径参数）
     * @return 教师信息
     */
    @GetMapping("/{teacherId}")
    public Result<Teacher> getById(@PathVariable Integer teacherId) {
        return Result.success(teacherService.getById(teacherId));
    }

    /**
     * 修改教师信息
     * @param teacherId 教师ID（路径参数）
     * @param teacher 教师信息（请求体）
     * @return 修改后的教师信息
     */
    @PutMapping("/{teacherId}")
    public Result<Teacher> update(@PathVariable Integer teacherId, @RequestBody Teacher teacher) {
        return Result.success(teacherService.update(teacherId, teacher));
    }

    /**
     * 删除教师
     * @param teacherId 教师ID（路径参数）
     * @return 操作结果
     */
    @DeleteMapping("/{teacherId}")
    public Result<Void> deleteById(@PathVariable Integer teacherId) {
        teacherService.deleteById(teacherId);
        return Result.success();
    }
}
