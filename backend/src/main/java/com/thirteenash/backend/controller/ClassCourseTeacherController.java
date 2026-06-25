package com.thirteenash.backend.controller;

import com.thirteenash.backend.entity.ClassCourseTeacher;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.result.Result;
import com.thirteenash.backend.pojo.vo.ClassCourseTeacherVO;
import com.thirteenash.backend.service.ClassCourseTeacherService;
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
@RequestMapping("/api/class-course-teachers")
public class ClassCourseTeacherController {

    private final ClassCourseTeacherService classCourseTeacherService;

    public ClassCourseTeacherController(ClassCourseTeacherService classCourseTeacherService) {
        this.classCourseTeacherService = classCourseTeacherService;
    }

    @GetMapping
    public Result<PageResult> page(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) Integer semesterId,
                                   @RequestParam(required = false) Integer classId,
                                   @RequestParam(required = false) Integer courseId,
                                   @RequestParam(required = false) Integer teacherId) {
        return Result.success(classCourseTeacherService.page(page, pageSize, semesterId, classId, courseId, teacherId));
    }

    @PostMapping
    public Result<ClassCourseTeacherVO> add(@RequestBody ClassCourseTeacher classCourseTeacher) {
        return Result.success(classCourseTeacherService.add(classCourseTeacher));
    }

    @GetMapping("/{cctId}")
    public Result<ClassCourseTeacherVO> getById(@PathVariable Integer cctId) {
        return Result.success(classCourseTeacherService.getById(cctId));
    }

    @PutMapping("/{cctId}")
    public Result<ClassCourseTeacherVO> update(@PathVariable Integer cctId,
                                               @RequestBody ClassCourseTeacher classCourseTeacher) {
        return Result.success(classCourseTeacherService.update(cctId, classCourseTeacher));
    }

    @DeleteMapping("/{cctId}")
    public Result<Void> deleteById(@PathVariable Integer cctId) {
        classCourseTeacherService.deleteById(cctId);
        return Result.success();
    }
}
