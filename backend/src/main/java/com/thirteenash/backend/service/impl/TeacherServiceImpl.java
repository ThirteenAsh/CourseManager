package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.Teacher;
import com.thirteenash.backend.mapper.TeacherMapper;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.service.TeacherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 教师管理服务实现类
 * 实现教师的增删改查业务逻辑，包含参数校验、工号唯一性检查及关联数据校验
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;

    public TeacherServiceImpl(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    /** 分页查询教师，支持按关键字（工号/姓名）和性别筛选 */
    @Override
    public PageResult page(Integer page, Integer pageSize, String keyword, String gender) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        String searchKeyword = trimToNull(keyword);
        String searchGender = trimToNull(gender);
        validateGender(searchGender, false);
        int offset = (currentPage - 1) * currentPageSize;

        Long total = teacherMapper.count(searchKeyword, searchGender);
        List<Teacher> records = teacherMapper.list(searchKeyword, searchGender, offset, currentPageSize);
        return new PageResult(total, records);
    }

    /** 新增教师，校验工号、姓名、性别非空且工号不重复 */
    @Override
    @Transactional
    public Teacher add(Teacher teacher) {
        validateTeacher(teacher);
        ensureTeacherNoNotExists(teacher.getTeacherNo(), null);
        teacherMapper.insert(teacher);
        return teacher;
    }

    /** 根据ID查询教师，不存在则抛出异常 */
    @Override
    public Teacher getById(Integer teacherId) {
        Teacher teacher = teacherMapper.getById(teacherId);
        if (teacher == null) {
            throw new IllegalArgumentException("教师不存在");
        }
        return teacher;
    }

    /** 修改教师信息，校验工号不重复，教师不存在则抛出异常 */
    @Override
    @Transactional
    public Teacher update(Integer teacherId, Teacher teacher) {
        if (teacherId == null) {
            throw new IllegalArgumentException("教师编号不能为空");
        }
        validateTeacher(teacher);
        ensureTeacherNoNotExists(teacher.getTeacherNo(), teacherId);

        teacher.setTeacherId(teacherId);
        Integer rows = teacherMapper.update(teacher);
        if (rows == null || rows == 0) {
            throw new IllegalArgumentException("教师不存在");
        }
        return teacherMapper.getById(teacherId);
    }

    /** 删除教师，存在任课关系时禁止删除 */
    @Override
    @Transactional
    public void deleteById(Integer teacherId) {
        if (teacherId == null) {
            throw new IllegalArgumentException("教师编号不能为空");
        }
        if (teacherMapper.getById(teacherId) == null) {
            throw new IllegalArgumentException("教师不存在");
        }
        Integer classCourseCount = teacherMapper.countClassCourseByTeacherId(teacherId);
        if (classCourseCount != null && classCourseCount > 0) {
            throw new IllegalArgumentException("该教师存在任课关系，不能删除");
        }
        teacherMapper.deleteById(teacherId);
    }

    /** 校验教师信息完整性：工号、姓名、性别非空 */
    private void validateTeacher(Teacher teacher) {
        if (teacher == null) {
            throw new IllegalArgumentException("教师信息不能为空");
        }
        String teacherNo = trimToNull(teacher.getTeacherNo());
        if (teacherNo == null) {
            throw new IllegalArgumentException("教师工号不能为空");
        }
        String teacherName = trimToNull(teacher.getTeacherName());
        if (teacherName == null) {
            throw new IllegalArgumentException("教师姓名不能为空");
        }
        String gender = trimToNull(teacher.getGender());
        validateGender(gender, true);

        teacher.setTeacherNo(teacherNo);
        teacher.setTeacherName(teacherName);
        teacher.setGender(gender);
    }

    /** 校验性别值是否合法（"男"或"女"），required 为 true 时不能为空 */
    private void validateGender(String gender, boolean required) {
        if (gender == null) {
            if (required) {
                throw new IllegalArgumentException("性别不能为空");
            }
            return;
        }
        if (!"男".equals(gender) && !"女".equals(gender)) {
            throw new IllegalArgumentException("性别只能是男或女");
        }
    }

    /** 检查教师工号是否已存在（排除自身ID），存在则抛出异常 */
    private void ensureTeacherNoNotExists(String teacherNo, Integer teacherId) {
        Integer count = teacherMapper.countByTeacherNo(teacherNo, teacherId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("教师工号已存在");
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
