package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.Course;
import com.thirteenash.backend.pojo.result.PageResult;

public interface CourseService {

    PageResult page(Integer page, Integer pageSize, String courseCode, String courseName);

    Course add(Course course);

    Course getById(Integer courseId);

    Course update(Integer courseId, Course course);

    void deleteById(Integer courseId);
}
