package com.thirteenash.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 年级实体类，对应数据库 grade 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade implements Serializable {

    /** 年级编号，主键 */
    private Integer gradeId;

    /** 年级名称，例如高一、高二、初三 */
    private String gradeName;
}
