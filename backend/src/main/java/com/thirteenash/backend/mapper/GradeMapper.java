package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.Grade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GradeMapper {

    List<Grade> list(@Param("gradeName") String gradeName,
                     @Param("offset") Integer offset,
                     @Param("pageSize") Integer pageSize);

    Long count(@Param("gradeName") String gradeName);

    Grade getById(Integer gradeId);

    Integer countByName(@Param("gradeName") String gradeName, @Param("gradeId") Integer gradeId);

    Integer countClassByGradeId(Integer gradeId);

    Integer insert(Grade grade);

    Integer update(Grade grade);

    Integer deleteById(Integer gradeId);
}
