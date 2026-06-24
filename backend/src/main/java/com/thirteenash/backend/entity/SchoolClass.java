package com.thirteenash.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 班级实体类，对应数据库 school_class 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClass implements Serializable {

    /** 班级编号，主键 */
    private Integer classId;

    /** 班级名称，例如高一1班 */
    private String className;

    /** 所属年级编号 */
    private Integer gradeId;

    /** 班主任教师编号 */
    private Integer headTeacherId;
}
