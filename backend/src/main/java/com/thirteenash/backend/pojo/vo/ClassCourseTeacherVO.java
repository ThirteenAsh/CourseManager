package com.thirteenash.backend.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassCourseTeacherVO implements Serializable {

    private Integer cctId;

    private Integer semesterId;

    private String schoolYear;

    private Integer term;

    private String termName;

    private Integer classId;

    private String className;

    private Integer courseId;

    private String courseCode;

    private String courseName;

    private Integer teacherId;

    private String teacherNo;

    private String teacherName;

    private Integer weeklyHours;
}
