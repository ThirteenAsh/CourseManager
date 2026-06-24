package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.Student;
import com.thirteenash.backend.pojo.vo.StudentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentMapper {

    List<StudentVO> list(@Param("studentNo") String studentNo,
                         @Param("studentName") String studentName,
                         @Param("gender") String gender,
                         @Param("classId") Integer classId,
                         @Param("offset") Integer offset,
                         @Param("pageSize") Integer pageSize);

    Long count(@Param("studentNo") String studentNo,
               @Param("studentName") String studentName,
               @Param("gender") String gender,
               @Param("classId") Integer classId);

    StudentVO getById(Integer studentId);

    Integer countByStudentNo(@Param("studentNo") String studentNo, @Param("studentId") Integer studentId);

    Integer insert(Student student);

    Integer update(Student student);

    Integer deleteById(Integer studentId);
}
