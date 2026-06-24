package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.Teacher;
import com.thirteenash.backend.pojo.result.PageResult;

public interface TeacherService {

    PageResult page(Integer page, Integer pageSize, String keyword, String gender);

    Teacher add(Teacher teacher);

    Teacher getById(Integer teacherId);

    Teacher update(Integer teacherId, Teacher teacher);

    void deleteById(Integer teacherId);
}
