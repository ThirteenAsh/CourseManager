package com.thirteenash.backend.controller;

import com.thirteenash.backend.entity.Period;
import com.thirteenash.backend.pojo.result.Result;
import com.thirteenash.backend.service.PeriodService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/periods")
public class PeriodController {

    private final PeriodService periodService;

    public PeriodController(PeriodService periodService) {
        this.periodService = periodService;
    }

    @GetMapping
    public Result<List<Period>> listAll() {
        return Result.success(periodService.listAll());
    }

    @PostMapping
    public Result<Period> add(@RequestBody Period period) {
        return Result.success(periodService.add(period));
    }

    @PutMapping("/{periodId}")
    public Result<Period> update(@PathVariable Integer periodId, @RequestBody Period period) {
        return Result.success(periodService.update(periodId, period));
    }

    @DeleteMapping("/{periodId}")
    public Result<Void> deleteById(@PathVariable Integer periodId) {
        periodService.deleteById(periodId);
        return Result.success();
    }
}
