package com.thirteenash.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 课程实体类，对应数据库 course 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course implements Serializable {

    /** 课程编号，主键 */
    private Integer courseId;

    /** 课程代码 */
    private String courseCode;

    /** 课程名称，例如语文、数学、英语 */
    private String courseName;
}
