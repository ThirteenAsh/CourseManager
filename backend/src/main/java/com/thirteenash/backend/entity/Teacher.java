package com.thirteenash.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教师实体类，对应数据库 teacher 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher implements Serializable {

    /** 教师编号，主键 */
    private Integer teacherId;

    /** 教师工号 */
    private String teacherNo;

    /** 教师姓名 */
    private String teacherName;

    /** 性别：男、女 */
    private String gender;
}
