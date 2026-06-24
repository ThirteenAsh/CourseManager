package com.thirteenash.backend.controller;

import com.thirteenash.backend.entity.SchoolClass;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.result.Result;
import com.thirteenash.backend.pojo.vo.SchoolClassVO;
import com.thirteenash.backend.service.SchoolClassService;
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
 * 班级管理控制器
 * 提供班级的增删改查 RESTful 接口
 */
@RestController
@RequestMapping("/api/classes")
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    public SchoolClassController(SchoolClassService schoolClassService) {
        this.schoolClassService = schoolClassService;
    }

    /**
     * 分页查询班级列表
     * @param page 页码（可选，默认第1页）
     * @param pageSize 每页条数（可选，默认10条）
     * @param className 班级名称关键字（可选，模糊查询）
     * @param gradeId 所属年级ID（可选）
     * @param headTeacherId 班主任教师ID（可选）
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult> page(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) String className,
                                   @RequestParam(required = false) Integer gradeId,
                                   @RequestParam(required = false) Integer headTeacherId) {
        return Result.success(schoolClassService.page(page, pageSize, className, gradeId, headTeacherId));
    }

    /**
     * 新增班级
     * @param schoolClass 班级信息（请求体）
     * @return 新增后的班级信息（含年级名称、班主任姓名）
     */
    @PostMapping
    public Result<SchoolClassVO> add(@RequestBody SchoolClass schoolClass) {
        return Result.success(schoolClassService.add(schoolClass));
    }

    /**
     * 根据ID查询班级详情
     * @param classId 班级ID（路径参数）
     * @return 班级详情（含年级名称、班主任姓名）
     */
    @GetMapping("/{classId}")
    public Result<SchoolClassVO> getById(@PathVariable Integer classId) {
        return Result.success(schoolClassService.getById(classId));
    }

    /**
     * 修改班级信息
     * @param classId 班级ID（路径参数）
     * @param schoolClass 班级信息（请求体）
     * @return 修改后的班级信息
     */
    @PutMapping("/{classId}")
    public Result<SchoolClassVO> update(@PathVariable Integer classId, @RequestBody SchoolClass schoolClass) {
        return Result.success(schoolClassService.update(classId, schoolClass));
    }

    /**
     * 删除班级
     * @param classId 班级ID（路径参数）
     * @return 操作结果
     */
    @DeleteMapping("/{classId}")
    public Result<Void> deleteById(@PathVariable Integer classId) {
        schoolClassService.deleteById(classId);
        return Result.success();
    }
}
