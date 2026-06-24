package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseMapper {

    List<Course> list(@Param("courseCode") String courseCode,
                      @Param("courseName") String courseName,
                      @Param("offset") Integer offset,
                      @Param("pageSize") Integer pageSize);

    Long count(@Param("courseCode") String courseCode, @Param("courseName") String courseName);

    Course getById(Integer courseId);

    Integer countByCourseCode(@Param("courseCode") String courseCode, @Param("courseId") Integer courseId);

    Integer countByCourseName(@Param("courseName") String courseName, @Param("courseId") Integer courseId);

    Integer countClassCourseByCourseId(Integer courseId);

    Integer insert(Course course);

    Integer update(Course course);

    Integer deleteById(Integer courseId);
}
