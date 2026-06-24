package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.Student;
import com.thirteenash.backend.mapper.SchoolClassMapper;
import com.thirteenash.backend.mapper.StudentMapper;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.vo.StudentVO;
import com.thirteenash.backend.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final SchoolClassMapper schoolClassMapper;

    public StudentServiceImpl(StudentMapper studentMapper, SchoolClassMapper schoolClassMapper) {
        this.studentMapper = studentMapper;
        this.schoolClassMapper = schoolClassMapper;
    }

    @Override
    public PageResult page(Integer page, Integer pageSize, String studentNo, String studentName, String gender, Integer classId) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        String searchStudentNo = trimToNull(studentNo);
        String searchStudentName = trimToNull(studentName);
        String searchGender = trimToNull(gender);
        validateGender(searchGender, false);
        int offset = (currentPage - 1) * currentPageSize;

        Long total = studentMapper.count(searchStudentNo, searchStudentName, searchGender, classId);
        List<StudentVO> records = studentMapper.list(searchStudentNo, searchStudentName, searchGender, classId, offset, currentPageSize);
        return new PageResult(total, records);
    }

    @Override
    @Transactional
    public StudentVO add(Student student) {
        validateStudent(student);
        ensureStudentNoNotExists(student.getStudentNo(), null);
        studentMapper.insert(student);
        return getById(student.getStudentId());
    }

    @Override
    public StudentVO getById(Integer studentId) {
        StudentVO student = studentMapper.getById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("学生不存在");
        }
        return student;
    }

    @Override
    @Transactional
    public StudentVO update(Integer studentId, Student student) {
        if (studentId == null) {
            throw new IllegalArgumentException("学生编号不能为空");
        }
        validateStudent(student);
        ensureStudentNoNotExists(student.getStudentNo(), studentId);

        student.setStudentId(studentId);
        Integer rows = studentMapper.update(student);
        if (rows == null || rows == 0) {
            throw new IllegalArgumentException("学生不存在");
        }
        return getById(studentId);
    }

    @Override
    @Transactional
    public void deleteById(Integer studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("学生编号不能为空");
        }
        if (studentMapper.getById(studentId) == null) {
            throw new IllegalArgumentException("学生不存在");
        }
        studentMapper.deleteById(studentId);
    }

    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("学生信息不能为空");
        }
        String studentNo = trimToNull(student.getStudentNo());
        if (studentNo == null) {
            throw new IllegalArgumentException("学号不能为空");
        }
        String studentName = trimToNull(student.getStudentName());
        if (studentName == null) {
            throw new IllegalArgumentException("学生姓名不能为空");
        }
        String gender = trimToNull(student.getGender());
        validateGender(gender, true);
        if (student.getClassId() == null) {
            throw new IllegalArgumentException("所属班级不能为空");
        }
        if (schoolClassMapper.getById(student.getClassId()) == null) {
            throw new IllegalArgumentException("所属班级不存在");
        }

        student.setStudentNo(studentNo);
        student.setStudentName(studentName);
        student.setGender(gender);
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

    private void ensureStudentNoNotExists(String studentNo, Integer studentId) {
        Integer count = studentMapper.countByStudentNo(studentNo, studentId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("学号已存在");
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
