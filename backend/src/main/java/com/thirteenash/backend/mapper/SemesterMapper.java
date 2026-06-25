package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.Semester;
import com.thirteenash.backend.pojo.vo.SemesterVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SemesterMapper {

    List<SemesterVO> list(@Param("schoolYear") String schoolYear,
                          @Param("term") Integer term,
                          @Param("isCurrent") Integer isCurrent,
                          @Param("offset") Integer offset,
                          @Param("pageSize") Integer pageSize);

    Long count(@Param("schoolYear") String schoolYear,
               @Param("term") Integer term,
               @Param("isCurrent") Integer isCurrent);

    SemesterVO getById(Integer semesterId);

    SemesterVO getCurrent();

    Integer countBySchoolYearAndTerm(@Param("schoolYear") String schoolYear,
                                     @Param("term") Integer term,
                                     @Param("semesterId") Integer semesterId);

    Integer countClassCourseBySemesterId(Integer semesterId);

    Integer insert(Semester semester);

    Integer update(Semester semester);

    Integer clearCurrent(@Param("semesterId") Integer semesterId);

    Integer deleteById(Integer semesterId);
}
