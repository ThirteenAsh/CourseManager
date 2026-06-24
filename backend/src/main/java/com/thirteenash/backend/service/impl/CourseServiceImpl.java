package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.Course;
import com.thirteenash.backend.mapper.CourseMapper;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    @Override
    public PageResult page(Integer page, Integer pageSize, String courseCode, String courseName) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        String searchCourseCode = trimToNull(courseCode);
        String searchCourseName = trimToNull(courseName);
        int offset = (currentPage - 1) * currentPageSize;

        Long total = courseMapper.count(searchCourseCode, searchCourseName);
        List<Course> records = courseMapper.list(searchCourseCode, searchCourseName, offset, currentPageSize);
        return new PageResult(total, records);
    }

    @Override
    @Transactional
    public Course add(Course course) {
        validateCourse(course);
        ensureCourseCodeNotExists(course.getCourseCode(), null);
        ensureCourseNameNotExists(course.getCourseName(), null);
        courseMapper.insert(course);
        return course;
    }

    @Override
    public Course getById(Integer courseId) {
        Course course = courseMapper.getById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        return course;
    }

    @Override
    @Transactional
    public Course update(Integer courseId, Course course) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程编号不能为空");
        }
        validateCourse(course);
        ensureCourseCodeNotExists(course.getCourseCode(), courseId);
        ensureCourseNameNotExists(course.getCourseName(), courseId);

        course.setCourseId(courseId);
        Integer rows = courseMapper.update(course);
        if (rows == null || rows == 0) {
            throw new IllegalArgumentException("课程不存在");
        }
        return courseMapper.getById(courseId);
    }

    @Override
    @Transactional
    public void deleteById(Integer courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("课程编号不能为空");
        }
        if (courseMapper.getById(courseId) == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        Integer classCourseCount = courseMapper.countClassCourseByCourseId(courseId);
        if (classCourseCount != null && classCourseCount > 0) {
            throw new IllegalArgumentException("该课程存在任课关系，不能删除");
        }
        courseMapper.deleteById(courseId);
    }

    private void validateCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("课程信息不能为空");
        }
        String courseCode = trimToNull(course.getCourseCode());
        if (courseCode == null) {
            throw new IllegalArgumentException("课程代码不能为空");
        }
        String courseName = trimToNull(course.getCourseName());
        if (courseName == null) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
    }

    private void ensureCourseCodeNotExists(String courseCode, Integer courseId) {
        Integer count = courseMapper.countByCourseCode(courseCode, courseId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("课程代码已存在");
        }
    }

    private void ensureCourseNameNotExists(String courseName, Integer courseId) {
        Integer count = courseMapper.countByCourseName(courseName, courseId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("课程名称已存在");
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
