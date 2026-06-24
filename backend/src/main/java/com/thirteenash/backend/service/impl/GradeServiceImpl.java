package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.Grade;
import com.thirteenash.backend.mapper.GradeMapper;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.service.GradeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GradeServiceImpl implements GradeService {

    private final GradeMapper gradeMapper;

    public GradeServiceImpl(GradeMapper gradeMapper) {
        this.gradeMapper = gradeMapper;
    }

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

    @Override
    @Transactional
    public void add(Grade grade) {
        validateGradeName(grade);
        ensureNameNotExists(grade.getGradeName(), null);
        gradeMapper.insert(grade);
    }

    @Override
    public Grade getById(Integer gradeId) {
        Grade grade = gradeMapper.getById(gradeId);
        if (grade == null) {
            throw new IllegalArgumentException("年级不存在");
        }
        return grade;
    }

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

    private void validateGradeName(Grade grade) {
        if (grade == null || trimToNull(grade.getGradeName()) == null) {
            throw new IllegalArgumentException("年级名称不能为空");
        }
        grade.setGradeName(grade.getGradeName().trim());
    }

    private void ensureNameNotExists(String gradeName, Integer gradeId) {
        Integer count = gradeMapper.countByName(gradeName, gradeId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("年级名称已存在");
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
