package com.thirteenash.backend.controller;

import com.thirteenash.backend.entity.Semester;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.result.Result;
import com.thirteenash.backend.pojo.vo.SemesterVO;
import com.thirteenash.backend.service.SemesterService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @GetMapping
    public Result<PageResult> page(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) String schoolYear,
                                   @RequestParam(required = false) Integer term,
                                   @RequestParam(required = false) Integer isCurrent) {
        return Result.success(semesterService.page(page, pageSize, schoolYear, term, isCurrent));
    }

    @PostMapping
    public Result<SemesterVO> add(@RequestBody Semester semester) {
        return Result.success(semesterService.add(semester));
    }

    @GetMapping("/current")
    public Result<SemesterVO> getCurrent() {
        return Result.success(semesterService.getCurrent());
    }

    @GetMapping("/{semesterId}")
    public Result<SemesterVO> getById(@PathVariable Integer semesterId) {
        return Result.success(semesterService.getById(semesterId));
    }

    @PutMapping("/{semesterId}")
    public Result<SemesterVO> update(@PathVariable Integer semesterId, @RequestBody Semester semester) {
        return Result.success(semesterService.update(semesterId, semester));
    }

    @DeleteMapping("/{semesterId}")
    public Result<Void> deleteById(@PathVariable Integer semesterId) {
        semesterService.deleteById(semesterId);
        return Result.success();
    }
}
