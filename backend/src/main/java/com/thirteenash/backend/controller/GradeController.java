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

/**
 * 年级管理控制器
 * 提供年级的增删改查 RESTful 接口
 */
@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    /**
     * 分页查询年级列表
     * @param page 页码（可选，默认第1页）
     * @param pageSize 每页条数（可选，默认10条）
     * @param gradeName 年级名称关键字（可选，模糊查询）
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult> page(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) String gradeName) {
        return Result.success(gradeService.page(page, pageSize, gradeName));
    }

    /**
     * 新增年级
     * @param grade 年级信息（请求体）
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> add(@RequestBody Grade grade) {
        gradeService.add(grade);
        return Result.success();
    }

    /**
     * 根据ID查询年级详情
     * @param gradeId 年级ID（路径参数）
     * @return 年级信息
     */
    @GetMapping("/{gradeId}")
    public Result<Grade> getById(@PathVariable Integer gradeId) {
        return Result.success(gradeService.getById(gradeId));
    }

    /**
     * 修改年级信息
     * @param gradeId 年级ID（路径参数）
     * @param grade 年级信息（请求体）
     * @return 操作结果
     */
    @PutMapping("/{gradeId}")
    public Result<Void> update(@PathVariable Integer gradeId, @RequestBody Grade grade) {
        gradeService.update(gradeId, grade);
        return Result.success();
    }

    /**
     * 删除年级
     * @param gradeId 年级ID（路径参数）
     * @return 操作结果
     */
    @DeleteMapping("/{gradeId}")
    public Result<Void> deleteById(@PathVariable Integer gradeId) {
        gradeService.deleteById(gradeId);
        return Result.success();
    }
}
