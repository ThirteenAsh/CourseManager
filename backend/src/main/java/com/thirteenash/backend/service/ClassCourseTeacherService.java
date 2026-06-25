package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.ClassCourseTeacher;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.vo.ClassCourseTeacherVO;

public interface ClassCourseTeacherService {

    PageResult page(Integer page, Integer pageSize, Integer semesterId, Integer classId, Integer courseId, Integer teacherId);

    ClassCourseTeacherVO add(ClassCourseTeacher classCourseTeacher);

    ClassCourseTeacherVO getById(Integer cctId);

    ClassCourseTeacherVO update(Integer cctId, ClassCourseTeacher classCourseTeacher);

    void deleteById(Integer cctId);
}
