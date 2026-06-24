package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.Grade;
import com.thirteenash.backend.mapper.GradeMapper;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.service.GradeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 年级管理服务实现类
 * 实现年级的增删改查业务逻辑，包含参数校验、名称唯一性检查及关联数据校验
 */
@Service
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;

    public GradeServiceImpl(GradeMapper gradeMapper) {
        this.gradeMapper = gradeMapper;
    }

    /** 分页查询年级，支持按名称模糊搜索 */
    @Override
    public PageResult page(Integer page, Integer pageSize, String gradeName) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        String keyword = trimToNull(gradeName);
        int offset = (currentPage - 1) * currentPageSize;

        Long total = gradeMapper.count(keyword);
        List<Grade> records = gradeMapper.list(keyword, offset, currentPageSize);
        return new PageResult(total, records);
    }

    /** 新增年级，校验名称非空且不重复 */
    @Override
    @Transactional
    public void add(Grade grade) {
        validateGradeName(grade);
        ensureNameNotExists(grade.getGradeName(), null);
        gradeMapper.insert(grade);
    }

    /** 根据ID查询年级，不存在则抛出异常 */
    @Override
    public Grade getById(Integer gradeId) {
        Grade grade = gradeMapper.getById(gradeId);
        if (grade == null) {
            throw new IllegalArgumentException("年级不存在");
        }
        return grade;
    }

    /** 修改年级信息，校验名称不重复，年级不存在则抛出异常 */
    @Override
    @Transactional
    public void update(Integer gradeId, Grade grade) {
        if (gradeId == null) {
            throw new IllegalArgumentException("年级编号不能为空");
        }
        validateGradeName(grade);
        ensureNameNotExists(grade.getGradeName(), gradeId);

        grade.setGradeId(gradeId);
        Integer rows = gradeMapper.update(grade);
        if (rows == null || rows == 0) {
            throw new IllegalArgumentException("年级不存在");
        }
    }

    /** 删除年级，存在关联班级时禁止删除 */
    @Override
    @Transactional
    public void deleteById(Integer gradeId) {
        if (gradeId == null) {
            throw new IllegalArgumentException("年级编号不能为空");
        }
        if (gradeMapper.getById(gradeId) == null) {
            throw new IllegalArgumentException("年级不存在");
        }
        Integer classCount = gradeMapper.countClassByGradeId(gradeId);
        if (classCount != null && classCount > 0) {
            throw new IllegalArgumentException("该年级下存在班级，不能删除");
        }
        gradeMapper.deleteById(gradeId);
    }

    /** 校验年级名称非空，并去除首尾空格 */
    private void validateGradeName(Grade grade) {
        if (grade == null || trimToNull(grade.getGradeName()) == null) {
            throw new IllegalArgumentException("年级名称不能为空");
        }
        grade.setGradeName(grade.getGradeName().trim());
    }

    /** 检查年级名称是否已存在（排除自身ID），存在则抛出异常 */
    private void ensureNameNotExists(String gradeName, Integer gradeId) {
        Integer count = gradeMapper.countByName(gradeName, gradeId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("年级名称已存在");
        }
    }

    /** 字符串去空格，空白字符串返回null */
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
