package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.Teacher;
import com.thirteenash.backend.mapper.TeacherMapper;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.service.TeacherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherMapper teacherMapper;

    public TeacherServiceImpl(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

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

    @Override
    @Transactional
    public Teacher add(Teacher teacher) {
        validateTeacher(teacher);
        ensureTeacherNoNotExists(teacher.getTeacherNo(), null);
        teacherMapper.insert(teacher);
        return teacher;
    }

    @Override
    public Teacher getById(Integer teacherId) {
        Teacher teacher = teacherMapper.getById(teacherId);
        if (teacher == null) {
            throw new IllegalArgumentException("教师不存在");
        }
        return teacher;
    }

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

    private void ensureTeacherNoNotExists(String teacherNo, Integer teacherId) {
        Integer count = teacherMapper.countByTeacherNo(teacherNo, teacherId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("教师工号已存在");
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
