package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.Period;

import java.util.List;

public interface PeriodService {

    List<Period> listAll();

    Period add(Period period);

    Period update(Integer periodId, Period period);

    void deleteById(Integer periodId);
}
