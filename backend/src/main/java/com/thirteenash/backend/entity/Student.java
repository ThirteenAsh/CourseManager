package com.thirteenash.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 学生实体类，对应数据库 student 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Serializable {

    /** 学生编号，主键 */
    private Integer studentId;

    /** 学号 */
    private String studentNo;

    /** 学生姓名 */
    private String studentName;

    /** 性别：男、女 */
    private String gender;

    /** 所属班级编号 */
    private Integer classId;
}
