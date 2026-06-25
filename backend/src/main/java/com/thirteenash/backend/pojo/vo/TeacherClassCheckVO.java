package com.thirteenash.backend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherClassCheckVO implements Serializable {

    private Integer hasClass;

    private Integer teacherId;

    private String teacherName;

    private String className;

    private String courseName;

    private Integer weekday;

    private String weekdayName;

    private Integer periodNo;

    private LocalTime startTime;

    private LocalTime endTime;

    private String classroom;
}
