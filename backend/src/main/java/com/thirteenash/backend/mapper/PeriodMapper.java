package com.thirteenash.backend.mapper;

import com.thirteenash.backend.entity.Period;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PeriodMapper {

    List<Period> listAll();

    Period getById(Integer periodId);

    Integer countByPeriodNo(@Param("periodNo") Integer periodNo, @Param("periodId") Integer periodId);

    Integer countTimetableByPeriodId(Integer periodId);

    Integer insert(Period period);

    Integer update(Period period);

    Integer deleteById(Integer periodId);
}
