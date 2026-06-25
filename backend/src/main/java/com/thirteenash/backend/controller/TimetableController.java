package com.thirteenash.backend.controller;

import com.thirteenash.backend.entity.Timetable;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.result.Result;
import com.thirteenash.backend.pojo.vo.ClassTimetableVO;
import com.thirteenash.backend.pojo.vo.TeacherClassCheckVO;
import com.thirteenash.backend.pojo.vo.TeacherTimetableVO;
import com.thirteenash.backend.pojo.vo.TimetableAddResultVO;
import com.thirteenash.backend.pojo.vo.TimetableVO;
import com.thirteenash.backend.service.TimetableService;
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
@RequestMapping("/api/timetables")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping("/check-teacher")
    public Result<TeacherClassCheckVO> checkTeacher(@RequestParam(required = false) Integer semesterId,
                                                    @RequestParam(required = false) Integer teacherId,
                                                    @RequestParam(required = false) Integer weekday,
                                                    @RequestParam(required = false) Integer periodId) {
        return Result.success(timetableService.checkTeacher(semesterId, teacherId, weekday, periodId));
    }

    @GetMapping("/class-table")
    public Result<ClassTimetableVO> getClassTable(@RequestParam(required = false) Integer semesterId,
                                                  @RequestParam(required = false) Integer classId) {
        return Result.success(timetableService.getClassTable(semesterId, classId));
    }

    @GetMapping("/teacher-table")
    public Result<TeacherTimetableVO> getTeacherTable(@RequestParam(required = false) Integer semesterId,
                                                      @RequestParam(required = false) Integer teacherId) {
        return Result.success(timetableService.getTeacherTable(semesterId, teacherId));
    }

    @GetMapping
    public Result<PageResult> page(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) Integer semesterId,
                                   @RequestParam(required = false) Integer classId,
                                   @RequestParam(required = false) Integer teacherId,
                                   @RequestParam(required = false) Integer courseId,
                                   @RequestParam(required = false) Integer weekday,
                                   @RequestParam(required = false) Integer periodId) {
        return Result.success(timetableService.page(page, pageSize, semesterId, classId, teacherId, courseId, weekday, periodId));
    }

    @PostMapping
    public Result<TimetableAddResultVO> add(@RequestBody Timetable timetable) {
        TimetableAddResultVO addResult = timetableService.add(timetable);
        Result<TimetableAddResultVO> result = Result.success(addResult);
        result.setMsg(addResult.getMessage());
        return result;
    }

    @GetMapping("/{timetableId}")
    public Result<TimetableVO> getById(@PathVariable Integer timetableId) {
        return Result.success(timetableService.getById(timetableId));
    }

    @PutMapping("/{timetableId}")
    public Result<TimetableVO> update(@PathVariable Integer timetableId, @RequestBody Timetable timetable) {
        return Result.success(timetableService.update(timetableId, timetable));
    }

    @DeleteMapping("/{timetableId}")
    public Result<Void> deleteById(@PathVariable Integer timetableId) {
        timetableService.deleteById(timetableId);
        return Result.success();
    }
}
