package com.thirteenash.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 学期实体类，对应数据库 semester 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semester implements Serializable {

    /** 学期编号，主键 */
    private Integer semesterId;

    /** 学年，例如2025-2026 */
    private String schoolYear;

    /** 学期：1表示上学期，2表示下学期 */
    private Integer term;

    /** 学期开始日期 */
    private LocalDate startDate;

    /** 学期结束日期 */
    private LocalDate endDate;

    /** 是否当前学期：1是，0否 */
    private Integer isCurrent;
}
