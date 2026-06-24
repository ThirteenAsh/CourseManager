package com.thirteenash.backend.controller;

import com.thirteenash.backend.entity.Student;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.result.Result;
import com.thirteenash.backend.pojo.vo.StudentVO;
import com.thirteenash.backend.service.StudentService;
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
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Result<PageResult> page(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) String studentNo,
                                   @RequestParam(required = false) String studentName,
                                   @RequestParam(required = false) String gender,
                                   @RequestParam(required = false) Integer classId) {
        return Result.success(studentService.page(page, pageSize, studentNo, studentName, gender, classId));
    }

    @PostMapping
    public Result<StudentVO> add(@RequestBody Student student) {
        return Result.success(studentService.add(student));
    }

    @GetMapping("/{studentId}")
    public Result<StudentVO> getById(@PathVariable Integer studentId) {
        return Result.success(studentService.getById(studentId));
    }

    @PutMapping("/{studentId}")
    public Result<StudentVO> update(@PathVariable Integer studentId, @RequestBody Student student) {
        return Result.success(studentService.update(studentId, student));
    }

    @DeleteMapping("/{studentId}")
    public Result<Void> deleteById(@PathVariable Integer studentId) {
        studentService.deleteById(studentId);
        return Result.success();
    }
}
