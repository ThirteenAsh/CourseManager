package com.thirteenash.backend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassTimetableRecordVO implements Serializable {

    private Integer weekday;

    private String weekdayName;

    private Integer periodNo;

    private LocalTime startTime;

    private LocalTime endTime;

    private String courseName;

    private String teacherName;

    private String classroom;

    private String remark;
}
