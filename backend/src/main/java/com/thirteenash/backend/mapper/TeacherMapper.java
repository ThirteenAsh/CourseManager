package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.Teacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeacherMapper {

    List<Teacher> list(@Param("keyword") String keyword,
                       @Param("gender") String gender,
                       @Param("offset") Integer offset,
                       @Param("pageSize") Integer pageSize);

    Long count(@Param("keyword") String keyword, @Param("gender") String gender);

    Teacher getById(Integer teacherId);

    Integer countByTeacherNo(@Param("teacherNo") String teacherNo, @Param("teacherId") Integer teacherId);

    Integer countClassCourseByTeacherId(Integer teacherId);

    Integer insert(Teacher teacher);

    Integer update(Teacher teacher);

    Integer deleteById(Integer teacherId);
}
