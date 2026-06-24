package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.SchoolClass;
import com.thirteenash.backend.mapper.GradeMapper;
import com.thirteenash.backend.mapper.SchoolClassMapper;
import com.thirteenash.backend.mapper.TeacherMapper;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.vo.SchoolClassVO;
import com.thirteenash.backend.service.SchoolClassService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 班级管理服务实现类
 * 实现班级的增删改查业务逻辑，包含参数校验、关联数据校验及级联检查
 */
@Service
public class SchoolClassServiceImpl implements SchoolClassService {

    private final SchoolClassMapper schoolClassMapper;
    private final GradeMapper gradeMapper;
    private final TeacherMapper teacherMapper;

    public SchoolClassServiceImpl(SchoolClassMapper schoolClassMapper,
                                  GradeMapper gradeMapper,
                                  TeacherMapper teacherMapper) {
        this.schoolClassMapper = schoolClassMapper;
        this.gradeMapper = gradeMapper;
        this.teacherMapper = teacherMapper;
    }

    /** 分页查询班级，支持按班级名称、年级、班主任筛选 */
    @Override
    public PageResult page(Integer page, Integer pageSize, String className, Integer gradeId, Integer headTeacherId) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        String searchClassName = trimToNull(className);
        int offset = (currentPage - 1) * currentPageSize;

        Long total = schoolClassMapper.count(searchClassName, gradeId, headTeacherId);
        List<SchoolClassVO> records = schoolClassMapper.list(searchClassName, gradeId, headTeacherId, offset, currentPageSize);
        return new PageResult(total, records);
    }

    /** 新增班级，校验年级和班主任是否存在，班级名称是否重复 */
    @Override
    @Transactional
    public SchoolClassVO add(SchoolClass schoolClass) {
        validateSchoolClass(schoolClass);
        ensureClassNameNotExists(schoolClass.getClassName(), null);
        schoolClassMapper.insert(schoolClass);
        return getById(schoolClass.getClassId());
    }

    /** 根据ID查询班级详情（含年级名称、班主任姓名），不存在则抛出异常 */
    @Override
    public SchoolClassVO getById(Integer classId) {
        SchoolClassVO schoolClass = schoolClassMapper.getById(classId);
        if (schoolClass == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        return schoolClass;
    }

    /** 修改班级信息，校验名称不重复及关联数据有效性 */
    @Override
    @Transactional
    public SchoolClassVO update(Integer classId, SchoolClass schoolClass) {
        if (classId == null) {
            throw new IllegalArgumentException("班级编号不能为空");
        }
        validateSchoolClass(schoolClass);
        ensureClassNameNotExists(schoolClass.getClassName(), classId);

        schoolClass.setClassId(classId);
        Integer rows = schoolClassMapper.update(schoolClass);
        if (rows == null || rows == 0) {
            throw new IllegalArgumentException("班级不存在");
        }
        return getById(classId);
    }

    /** 删除班级，存在关联学生或任课关系时禁止删除 */
    @Override
    @Transactional
    public void deleteById(Integer classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级编号不能为空");
        }
        if (schoolClassMapper.getById(classId) == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        Integer studentCount = schoolClassMapper.countStudentByClassId(classId);
        if (studentCount != null && studentCount > 0) {
            throw new IllegalArgumentException("该班级下存在学生，不能删除");
        }
        Integer classCourseCount = schoolClassMapper.countClassCourseByClassId(classId);
        if (classCourseCount != null && classCourseCount > 0) {
            throw new IllegalArgumentException("该班级存在任课关系，不能删除");
        }
        schoolClassMapper.deleteById(classId);
    }

    /** 校验班级信息完整性：名称、所属年级、班主任非空且存在 */
    private void validateSchoolClass(SchoolClass schoolClass) {
        if (schoolClass == null) {
            throw new IllegalArgumentException("班级信息不能为空");
        }
        String className = trimToNull(schoolClass.getClassName());
        if (className == null) {
            throw new IllegalArgumentException("班级名称不能为空");
        }
        if (schoolClass.getGradeId() == null) {
            throw new IllegalArgumentException("所属年级不能为空");
        }
        if (gradeMapper.getById(schoolClass.getGradeId()) == null) {
            throw new IllegalArgumentException("所属年级不存在");
        }
        if (schoolClass.getHeadTeacherId() == null) {
            throw new IllegalArgumentException("班主任不能为空");
        }
        if (teacherMapper.getById(schoolClass.getHeadTeacherId()) == null) {
            throw new IllegalArgumentException("班主任教师不存在");
        }
        schoolClass.setClassName(className);
    }

    /** 检查班级名称是否已存在（排除自身ID），存在则抛出异常 */
    private void ensureClassNameNotExists(String className, Integer classId) {
        Integer count = schoolClassMapper.countByClassName(className, classId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("班级名称已存在");
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
