package com.thirteenash.backend.service.impl;

import com.thirteenash.backend.entity.Period;
import com.thirteenash.backend.mapper.PeriodMapper;
import com.thirteenash.backend.service.PeriodService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PeriodServiceImpl implements PeriodService {

    private final PeriodMapper periodMapper;

    public PeriodServiceImpl(PeriodMapper periodMapper) {
        this.periodMapper = periodMapper;
    }

    @Override
    public List<Period> listAll() {
        return periodMapper.listAll();
    }

    @Override
    @Transactional
    public Period add(Period period) {
        validatePeriod(period);
        ensurePeriodNoNotExists(period.getPeriodNo(), null);
        periodMapper.insert(period);
        return getById(period.getPeriodId());
    }

    @Override
    @Transactional
    public Period update(Integer periodId, Period period) {
        if (periodId == null) {
            throw new IllegalArgumentException("节次编号不能为空");
        }
        validatePeriod(period);
        ensurePeriodNoNotExists(period.getPeriodNo(), periodId);

        period.setPeriodId(periodId);
        Integer rows = periodMapper.update(period);
        if (rows == null || rows == 0) {
            throw new IllegalArgumentException("节次不存在");
        }
        return getById(periodId);
    }

    @Override
    @Transactional
    public void deleteById(Integer periodId) {
        if (periodId == null) {
            throw new IllegalArgumentException("节次编号不能为空");
        }
        if (periodMapper.getById(periodId) == null) {
            throw new IllegalArgumentException("节次不存在");
        }
        Integer timetableCount = periodMapper.countTimetableByPeriodId(periodId);
        if (timetableCount != null && timetableCount > 0) {
            throw new IllegalArgumentException("该节次存在排课记录，不能删除");
        }
        periodMapper.deleteById(periodId);
    }

    private Period getById(Integer periodId) {
        Period period = periodMapper.getById(periodId);
        if (period == null) {
            throw new IllegalArgumentException("节次不存在");
        }
        return period;
    }

    private void validatePeriod(Period period) {
        if (period == null) {
            throw new IllegalArgumentException("节次信息不能为空");
        }
        if (period.getPeriodNo() == null) {
            throw new IllegalArgumentException("节次序号不能为空");
        }
        if (period.getPeriodNo() <= 0) {
            throw new IllegalArgumentException("节次序号必须大于0");
        }
        if (period.getStartTime() == null) {
            throw new IllegalArgumentException("上课时间不能为空");
        }
        if (period.getEndTime() == null) {
            throw new IllegalArgumentException("下课时间不能为空");
        }
        if (!period.getEndTime().isAfter(period.getStartTime())) {
            throw new IllegalArgumentException("下课时间必须晚于上课时间");
        }

        String remark = trimToNull(period.getRemark());
        if (remark == null) {
            throw new IllegalArgumentException("备注不能为空");
        }
        period.setRemark(remark);
    }

    private void ensurePeriodNoNotExists(Integer periodNo, Integer periodId) {
        Integer count = periodMapper.countByPeriodNo(periodNo, periodId);
        if (count != null && count > 0) {
            throw new IllegalArgumentException("该节次序号已存在");
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
