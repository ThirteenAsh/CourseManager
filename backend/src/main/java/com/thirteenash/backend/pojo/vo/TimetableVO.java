package com.thirteenash.backend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableVO implements Serializable {

    private Integer timetableId;

    private Integer cctId;

    private Integer semesterId;

    private String schoolYear;

    private Integer term;

    private String termName;

    private Integer classId;

    private String className;

    private Integer courseId;

    private String courseName;

    private Integer teacherId;

    private String teacherName;

    private Integer weekday;

    private String weekdayName;

    private Integer periodId;

    private Integer periodNo;

    private LocalTime startTime;

    private LocalTime endTime;

    private String classroom;

    private String remark;
}
