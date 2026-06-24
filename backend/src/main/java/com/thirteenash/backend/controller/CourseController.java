package com.thirteenash.backend.controller;

import com.thirteenash.backend.entity.Course;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.result.Result;
import com.thirteenash.backend.service.CourseService;
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
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public Result<PageResult> page(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) String courseCode,
                                   @RequestParam(required = false) String courseName) {
        return Result.success(courseService.page(page, pageSize, courseCode, courseName));
    }

    @PostMapping
    public Result<Course> add(@RequestBody Course course) {
        return Result.success(courseService.add(course));
    }

    @GetMapping("/{courseId}")
    public Result<Course> getById(@PathVariable Integer courseId) {
        return Result.success(courseService.getById(courseId));
    }

    @PutMapping("/{courseId}")
    public Result<Course> update(@PathVariable Integer courseId, @RequestBody Course course) {
        return Result.success(courseService.update(courseId, course));
    }

    @DeleteMapping("/{courseId}")
    public Result<Void> deleteById(@PathVariable Integer courseId) {
        courseService.deleteById(courseId);
        return Result.success();
    }
}
