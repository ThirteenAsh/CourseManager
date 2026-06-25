package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.ClassCourseTeacher;
import com.thirteenash.backend.pojo.vo.ClassCourseTeacherVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClassCourseTeacherMapper {

    List<ClassCourseTeacherVO> list(@Param("semesterId") Integer semesterId,
                                    @Param("classId") Integer classId,
                                    @Param("courseId") Integer courseId,
                                    @Param("teacherId") Integer teacherId,
                                    @Param("offset") Integer offset,
                                    @Param("pageSize") Integer pageSize);

    Long count(@Param("semesterId") Integer semesterId,
               @Param("classId") Integer classId,
               @Param("courseId") Integer courseId,
               @Param("teacherId") Integer teacherId);

    ClassCourseTeacherVO getById(Integer cctId);

    Integer countBySemesterClassCourse(@Param("semesterId") Integer semesterId,
                                       @Param("classId") Integer classId,
                                       @Param("courseId") Integer courseId,
                                       @Param("cctId") Integer cctId);

    Integer insert(ClassCourseTeacher classCourseTeacher);

    Integer update(ClassCourseTeacher classCourseTeacher);

    Integer deleteById(Integer cctId);
}
