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

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public Result<PageResult> page(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String gender) {
        return Result.success(teacherService.page(page, pageSize, keyword, gender));
    }

    @PostMapping
    public Result<Teacher> add(@RequestBody Teacher teacher) {
        return Result.success(teacherService.add(teacher));
    }

    @GetMapping("/{teacherId}")
    public Result<Teacher> getById(@PathVariable Integer teacherId) {
        return Result.success(teacherService.getById(teacherId));
    }

    @PutMapping("/{teacherId}")
    public Result<Teacher> update(@PathVariable Integer teacherId, @RequestBody Teacher teacher) {
        return Result.success(teacherService.update(teacherId, teacher));
    }

    @DeleteMapping("/{teacherId}")
    public Result<Void> deleteById(@PathVariable Integer teacherId) {
        teacherService.deleteById(teacherId);
        return Result.success();
    }
}
