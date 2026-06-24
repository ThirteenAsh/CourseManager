package com.thirteenash.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 班级课程任课实体类，对应数据库 class_course_teacher 表
 * 记录某学期、某班级、某课程由哪位教师任课
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassCourseTeacher implements Serializable {

    /** 班级课程任课编号，主键 */
    private Integer cctId;

    /** 学期编号 */
    private Integer semesterId;

    /** 班级编号 */
    private Integer classId;

    /** 课程编号 */
    private Integer courseId;

    /** 任课教师编号 */
    private Integer teacherId;

    /** 每周课时数 */
    private Integer weeklyHours;
}
