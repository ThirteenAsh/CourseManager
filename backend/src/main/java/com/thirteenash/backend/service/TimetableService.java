package com.thirteenash.backend.service;

import com.thirteenash.backend.entity.Timetable;
import com.thirteenash.backend.pojo.result.PageResult;
import com.thirteenash.backend.pojo.vo.ClassTimetableVO;
import com.thirteenash.backend.pojo.vo.TeacherClassCheckVO;
import com.thirteenash.backend.pojo.vo.TeacherTimetableVO;
import com.thirteenash.backend.pojo.vo.TimetableAddResultVO;
import com.thirteenash.backend.pojo.vo.TimetableVO;

public interface TimetableService {

    PageResult page(Integer page, Integer pageSize, Integer semesterId, Integer classId, Integer teacherId,
                    Integer courseId, Integer weekday, Integer periodId);

    TimetableAddResultVO add(Timetable timetable);

    TimetableVO getById(Integer timetableId);

    TimetableVO update(Integer timetableId, Timetable timetable);

    void deleteById(Integer timetableId);

    TeacherClassCheckVO checkTeacher(Integer semesterId, Integer teacherId, Integer weekday, Integer periodId);

    ClassTimetableVO getClassTable(Integer semesterId, Integer classId);

    TeacherTimetableVO getTeacherTable(Integer semesterId, Integer teacherId);

    byte[] exportClassTable(Integer semesterId, Integer classId);

    byte[] exportTeacherTable(Integer semesterId, Integer teacherId);
}
