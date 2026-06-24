package com.thirteenash.backend.controller;

import com.thirteenash.backend.entity.Grade;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.result.Result;
import com.thirteenash.backend.service.GradeService;
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
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    public Result<PageResult> page(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) String gradeName) {
        return Result.success(gradeService.page(page, pageSize, gradeName));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Grade grade) {
        gradeService.add(grade);
        return Result.success();
    }

    @GetMapping("/{gradeId}")
    public Result<Grade> getById(@PathVariable Integer gradeId) {
        return Result.success(gradeService.getById(gradeId));
    }

    @PutMapping("/{gradeId}")
    public Result<Void> update(@PathVariable Integer gradeId, @RequestBody Grade grade) {
        gradeService.update(gradeId, grade);
        return Result.success();
    }

    @DeleteMapping("/{gradeId}")
    public Result<Void> deleteById(@PathVariable Integer gradeId) {
        gradeService.deleteById(gradeId);
        return Result.success();
    }
}
