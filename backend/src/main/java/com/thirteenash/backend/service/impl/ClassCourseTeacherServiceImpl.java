package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.ClassCourseTeacher;
import com.thirteenash.backend.mapper.ClassCourseTeacherMapper;
import com.thirteenash.backend.mapper.CourseMapper;
import com.thirteenash.backend.mapper.SchoolClassMapper;
import com.thirteenash.backend.mapper.SemesterMapper;
import com.thirteenash.backend.mapper.TeacherMapper;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.vo.ClassCourseTeacherVO;
import com.thirteenash.backend.service.ClassCourseTeacherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassCourseTeacherServiceImpl implements ClassCourseTeacherService {

    private final ClassCourseTeacherMapper classCourseTeacherMapper;
    private final SemesterMapper semesterMapper;
    private final SchoolClassMapper schoolClassMapper;
    private final CourseMapper courseMapper;
    private final TeacherMapper teacherMapper;

    public ClassCourseTeacherServiceImpl(ClassCourseTeacherMapper classCourseTeacherMapper,
                                         SemesterMapper semesterMapper,
                                         SchoolClassMapper schoolClassMapper,
                                         CourseMapper courseMapper,
                                         TeacherMapper teacherMapper) {
        this.classCourseTeacherMapper = classCourseTeacherMapper;
        this.semesterMapper = semesterMapper;
        this.schoolClassMapper = schoolClassMapper;
        this.courseMapper = courseMapper;
        this.teacherMapper = teacherMapper;
    }

    @Override
    public PageResult page(Integer page, Integer pageSize, Integer semesterId, Integer classId, Integer courseId, Integer teacherId) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (currentPage - 1) * currentPageSize;

        Long total = classCourseTeacherMapper.count(semesterId, classId, courseId, teacherId);
        List<ClassCourseTeacherVO> records = classCourseTeacherMapper.list(semesterId, classId, courseId, teacherId, offset, currentPageSize);
        return new PageResult(total, records);
    }

    @Override
    @Transactional
    public ClassCourseTeacherVO add(ClassCourseTeacher classCourseTeacher) {
        validateClassCourseTeacher(classCourseTeacher);
        ensureClassCourseTeacherNotExists(classCourseTeacher.getSemesterId(),
                classCourseTeacher.getClassId(),
                classCourseTeacher.getCourseId(),
                null);
        classCourseTeacherMapper.insert(classCourseTeacher);
        return getById(classCourseTeacher.getCctId());
    }

    @Override
    public ClassCourseTeacherVO getById(Integer cctId) {
        ClassCourseTeacherVO classCourseTeacher = classCourseTeacherMapper.getById(cctId);
        if (classCourseTeacher == null) {
            throw new IllegalArgumentException("任课关系不存在");
        }
        return classCourseTeacher;
    }

    @Override
    @Transactional
    public ClassCourseTeacherVO update(Integer cctId, ClassCourseTeacher classCourseTeacher) {
        if (cctId == null) {
            throw new IllegalArgumentException("任课关系编号不能为空");
        }
        validateClassCourseTeacher(classCourseTeacher);
        ensureClassCourseTeacherNotExists(classCourseTeacher.getSemesterId(),
                classCourseTeacher.getClassId(),
                classCourseTeacher.getCourseId(),
                cctId);

        classCourseTeacher.setCctId(cctId);
        Integer rows = classCourseTeacherMapper.update(classCourseTeacher);
        if (rows == null || rows == 0) {
            throw new IllegalArgumentException("任课关系不存在");
        }
        return getById(cctId);
    }

    @Override
    @Transactional
    public void deleteById(Integer cctId) {
        if (cctId == null) {
            throw new IllegalArgumentException("任课关系编号不能为空");
        }
        if (classCourseTeacherMapper.getById(cctId) == null) {
            throw new IllegalArgumentException("任课关系不存在");
        }
        classCourseTeacherMapper.deleteById(cctId);
    }

    private void validateClassCourseTeacher(ClassCourseTeacher classCourseTeacher) {
        if (classCourseTeacher == null) {
            throw new IllegalArgumentException("任课关系信息不能为空");
        }
        if (classCourseTeacher.getSemesterId() == null) {
            throw new IllegalArgumentException("学期编号不能为空");
        }
        if (semesterMapper.getById(classCourseTeacher.getSemesterId()) == null) {
            throw new IllegalArgumentException("学期不存在");
        }
        if (classCourseTeacher.getClassId() == null) {
            throw new IllegalArgumentException("班级编号不能为空");
        }
        if (schoolClassMapper.getById(classCourseTeacher.getClassId()) == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        if (classCourseTeacher.getCourseId() == null) {
            throw new IllegalArgumentException("课程编号不能为空");
        }
        if (courseMapper.getById(classCourseTeacher.getCourseId()) == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        if (classCourseTeacher.getTeacherId() == null) {
            throw new IllegalArgumentException("教师编号不能为空");
        }
        if (teacherMapper.getById(classCourseTeacher.getTeacherId()) == null) {
            throw new IllegalArgumentException("教师不存在");
        }
        if (classCourseTeacher.getWeeklyHours() == null) {
            throw new IllegalArgumentException("每周课时数不能为空");
        }
        if (classCourseTeacher.getWeeklyHours() <= 0) {
            throw new IllegalArgumentException("每周课时数必须大于0");
        }
    }

    private void ensureClassCourseTeacherNotExists(Integer semesterId, Integer classId, Integer courseId, Integer cctId) {
        Integer count = classCourseTeacherMapper.countBySemesterClassCourse(semesterId, classId, courseId, cctId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("该学期该班级的课程任课关系已存在");
        }
    }
}
